# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

불티(Boolti)는 밴드 공연 등록·예매·QR 입장 관리 Android 앱이다. Jetpack Compose + Hilt + 멀티 모듈 클린 아키텍처로 되어 있다.

- Play Store: https://play.google.com/store/apps/details?id=com.nexters.boolti
- App Store: https://apps.apple.com/kr/app/%EB%B6%88%ED%8B%B0/id6476589322
- Host web: https://boolti.in

## 모듈 구조

```
app/              - Application, DI 설정, Crashlytics/Timber 초기화
domain/           - Repository 인터페이스, UseCase, 모델 — 순수 Kotlin (JVM 모듈)
data/             - API(Retrofit), Room, DataStore, Repository 구현
presentation/     - Compose 화면, ViewModel
tosspayments/     - 토스페이먼츠 결제 위젯 Activity
common/logger/    - 디버그 로그 수집 (CollectableDebugTree)
common/tracker/   - Mixpanel 이벤트 트래킹 (AppTracker)
lint/             - 커스텀 Android Lint 규칙 (presentation에 lintChecks로 연결)
```

- 의존 방향: `presentation → domain ← data`
- 기능별로 패키지를 나눈다 (`presentation/screen/<기능>/`)
- 모듈별 세부 규칙은 `.claude/rules/`에 있고, 해당 모듈 파일을 다룰 때 불러온다

## 명령어

```bash
./gradlew btTest            # 전체 모듈 테스트 (CI와 같은 명령)
./gradlew assembleDebug     # 디버그 APK
./gradlew domain:test
./gradlew data:testDebugUnitTest
./gradlew presentation:testDebugUnitTest
./gradlew koverHtmlReportUnit  # 커버리지 리포트 (build/reports/kover/htmlUnit), UI·자동 생성 코드 제외
```

- 테스트: Kotest + MockK
- 버전은 `gradle/libs.versions.toml`에서 확인한다

## 테스트 작성 원칙

- 쓰기 전에 검증할 가치가 있는 로직인지 먼저 판단한다. 클래스 종류가 아니라 로직으로 판단한다
  - 쓴다: 분기·계산·상태 전이·에러 처리·데이터 변환처럼 틀리기 쉬운 로직
  - 안 쓴다: 값만 전달하는 위임, 단순 getter·data class, 라이브러리 동작 확인처럼 명백한 코드
- Kover 커버리지는 빠진 곳을 찾는 참고 지표다. 수치를 올리는 것 자체를 목표로 두지 않는다
- 프로덕션 코드는 테스트하기 쉬운 구조로 설계한다
  - 의존성은 생성자로 주입받는다. 클래스 안에서 직접 만들거나 상태를 가진 `object`·전역 상태에 기대지 않는다. `Dispatchers.IO`/`Default`도 주입받는다
  - 현재 시각·랜덤 값은 인자로 받는다 (예: `now: LocalDateTime = LocalDateTime.now()`)
  - 계산·판단 로직은 Android 클래스(`Context`, `Uri` 등)와 분리해 순수 Kotlin 코드로 둔다
- 좋은 테스트는 F.I.R.S.T.(빠름·독립·반복 가능·자동 판정·적시)를 지킨다
  - 실제 네트워크·DB·`delay` 대기에 기대지 않는다
  - 테스트 대상 로직을 바꾼 PR에는 테스트도 같은 PR로 넣는다
- 구현 방식이 아니라 동작(입력 → 결과·상태)을 검증한다. 내부 리팩토링으로 깨지는 테스트는 잘못 쓴 것이다

## 공통 규칙

- 금지
  - `!!`
  - 값이 없는 상태를 가짜 값(`"-999"` 등)으로 표현하기. `null`이나 별도 상태로 표현한다
  - 전체 경로 클래스 이름 (import로 해결)
  - 개인 디버그 로그 커밋 (디버깅 중 추가는 괜찮지만 커밋 전에 반드시 지운다)
- `runBlocking`은 호출한 스레드를 멈추므로 쓰지 않는다. DataStore 값을 동기로 읽어야 할 때처럼 불가피한 경우에만 한시적으로 쓴다
- 새 유틸을 만들기 전에 각 모듈의 `util` 패키지에 이미 있는지 먼저 찾는다
- 처리한 에러는 `Timber.e(e)`로 남긴다 (Crashlytics로 올라감, `IOException`·취소 예외 제외). 원격 기록이 필요 없으면 `Timber.w`를 쓴다

## 커밋

- 형식: `[Boolti-<이슈 번호>] <요약>` (예: `[Boolti-548] 프로필 이미지 복사를 IO 디스패처로 옮기기`)
- `feat:`, `fix:` 같은 타입 접두사는 붙이지 않는다. 변경 종류는 PR 레이블로 구분한다
- 이슈가 없으면 대괄호 없이 요약만 쓴다
- 이슈 번호는 브랜치 이름에서 가져온다 (`feature/548-xxx`, `feature/Boolti-548` → `548`)

## 릴리즈

- 정기 릴리즈는 `develop` → `release/<버전>`, 핫픽스는 `main` → `hotfix/<버전>`으로 나가고, 둘 다 `main`에 머지한 뒤 `v<버전>` 태그를 달고 `develop`에 역머지한다
- 시작·마무리·취소는 `.github/scripts/release/`의 스크립트로만 한다. 사용법은 그 폴더의 `README.md`에 있다
- 버전은 `gradle/libs.versions.toml`의 `versionName`만 관리한다. versionCode는 `app/app-version.gradle`이 계산한다 (`major * 100000 + minor * 100 + patch`)
- 테스터 배포(App Distribution)는 `boolti-distribution` 스킬을 쓴다

## CI (PR에서 실행)

`pull-request-ci`, `anti-pattern-check`는 `develop`·`feature/**` 대상 PR에서 돈다.


| 워크플로 | 확인 내용 |
|---|---|
| `pull-request-ci` | `./gradlew btTest`, `assembleDebug`, APK 크기 비교. 실패 시 디스코드 알림 |
| `anti-pattern-check` | data 레이어에 `runCatching` 추가 여부 (`suspendRunCatching`만 허용) |
| `pr-milestone-required` | PR에 마일스톤 지정 여부 |
| `release-version-check` | `release/*`·`hotfix/*` PR의 versionName (브랜치 이름과 같고 main보다 큰지) |

## Firebase

- 사용 중: Analytics, Crashlytics, Cloud Messaging(`BtFirebaseMessagingService`), Remote Config(`RemoteConfigDataSource`)
- 디버그 앱(`com.nexters.boolti.debug`)은 Firebase에 별도 앱으로 등록돼 있어 운영 데이터와 섞이지 않는다
- App Distribution 배포 정보는 `boolti-app-distribution` 스킬에 있다
