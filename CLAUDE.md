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
```

- 테스트: Kotest + MockK
- 버전은 `gradle/libs.versions.toml`에서 확인한다

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

## CI (PR에서 실행)

`pull-request-ci`, `anti-pattern-check`는 `develop`·`feature/**` 대상 PR에서 돈다.


| 워크플로 | 확인 내용 |
|---|---|
| `pull-request-ci` | `./gradlew btTest`, `assembleDebug`, APK 크기 비교. 실패 시 디스코드 알림 |
| `anti-pattern-check` | data 레이어에 `runCatching` 추가 여부 (`suspendRunCatching`만 허용) |
| `pr-milestone-required` | PR에 마일스톤 지정 여부 |
| `release-version-check` | 릴리즈 PR의 versionCode·versionName |

## Firebase

- 사용 중: Analytics, Crashlytics, Cloud Messaging(`BtFirebaseMessagingService`), Remote Config(`RemoteConfigDataSource`)
- 디버그 앱(`com.nexters.boolti.debug`)은 Firebase에 별도 앱으로 등록돼 있어 운영 데이터와 섞이지 않는다
- App Distribution 배포 정보는 `boolti-app-distribution` 스킬에 있다
