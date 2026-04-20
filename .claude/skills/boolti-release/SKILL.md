---
name: boolti-release
description: 불티 Android 앱 릴리즈 오케스트레이터. 배포 경로 선택 → Pre-release 검증 → 버전 상향 → QA 친화 릴리즈 노트 초안 생성까지 수행한 뒤, 사용자 승인을 받으면 배포 경로별 sub-skill(`boolti-app-distribution`, 추후 `boolti-play-console`)에 위임한다. "릴리즈 준비해줘", "테스터 배포해줘", "릴리즈 노트 써줘", "버전 올려줘", "릴리즈 빌드" 등 릴리즈 전체 흐름을 요청할 때 트리거.
---

# 불티 릴리즈 오케스트레이터

불티 Android 앱 릴리즈의 **공통 전처리**(경로 선택, 검증, 버전 상향, 릴리즈 노트 생성)를 담당한다. 빌드·업로드 같은 경로별 작업은 sub-skill로 위임한다.

## 스킬 분리 구조

| 스킬 | 역할 |
|------|------|
| **`boolti-release` (본 스킬)** | 오케스트레이션. 공통 전처리만 담당 |
| `boolti-app-distribution` | Firebase App Distribution 업로드 전담 (단독 호출 가능) |
| `boolti-play-console` | Play Console 업로드 전담 (현재 미구현) |

## 원칙

- Step 0에서 배포 경로를 먼저 확정한다. 경로 전에 빌드·업로드는 금지.
- 릴리즈 노트는 파일로 저장하지 않는다. 브랜치 변경사항에서 초안을 뽑아 사용자 확인 후 sub-skill에 **인라인 텍스트로 전달**.
- 릴리즈 노트 독자는 QA 매니저. 내부 구현 용어(`ViewModel`, `StateFlow`, `Repository`, `Hilt` 등) 금지. 사용자가 앱에서 보거나 누르거나 체감하는 기능만 서술.
- 운영 배포 브랜치는 `qa/*` 또는 `release/*`. `develop`/`main`에서 직접 릴리즈 빌드하지 않는다.

## Step 0. 배포 경로 선택 (blocking)

> 1. **App Distribution** (테스터 배포)
> 2. **Play Console** (운영 배포)

- **1** → Step 1 진행. Step 5에서 `boolti-app-distribution` 위임.
- **2** → 아래 안내 후 즉시 종료.
  > Play Console 자동 배포는 미지원(향후 `boolti-play-console`로 추가 예정). AAB 빌드 후 [Play Console](https://play.google.com/console) 수동 업로드 필요.
  >
  > ```bash
  > ./gradlew clean bundleRelease
  > ```

## Step 1. Pre-release 검증 (blocking)

| 검증 | 명령 | 실패 시 |
|------|------|---------|
| 릴리즈용 브랜치 (`qa/*` · `release/*` · `feature/*` · `fix/*`) | `git branch --show-current` | `develop`·`main`이면 중단 |
| 워킹 트리 clean | `git status --porcelain` | 사용자에게 커밋/스태시 확인 (임의 커밋 금지) |
| `local.properties` 존재 | `test -f local.properties` | 팀 비공개 채널에서 값 확보 안내 |
| `keystore.properties` 존재 | `test -f keystore.properties` | release APK 빌드 시에만 필수 (debug 배포면 skip) |
| 버전 확인 | `grep -E 'versionCode\|versionName' gradle/libs.versions.toml` | 값 보고 후 Step 2 |

## Step 2. 버전 상향

`gradle/libs.versions.toml`의 `[versions]`에서 `versionCode`(+1), `versionName`(SemVer)을 갱신.

사용자에게는 근거와 함께 제안: "현재 `1.14.1` → 신규 기능 2건, 버그 수정 3건 → `1.15.0` 제안".

## Step 3. 릴리즈 노트 초안 생성 (핵심)

### 3-1. 브랜치 유형별 diff 기준

| 브랜치 패턴 | Diff 기준 |
|-------------|-----------|
| `feature/*`, `fix/*`, `qa/*` | `develop` |
| `release/*` | 마지막 릴리즈 태그 (`git describe --tags --abbrev=0`) |
| 그 외 | 사용자에게 base 브랜치 확인 |

### 3-2. 변경 내역 수집

```bash
git log <base>..HEAD --pretty=format:'%s' --no-merges
```

`release/*`에서 태그가 없으면 `develop`으로 fallback하고 사용자에게 확인.

### 3-3. 커밋 분류·필터링

- **포함**: `feat:` → **주요 업데이트**, `fix:` → **버그 수정**
- **제외**: `refactor:`, `chore:`, `docs:`, `test:`, `ci:`, `style:`, `perf:`, Mixpanel 이벤트, 의존성/빌드 설정

### 3-4. QA 친화 변환

커밋 메시지를 그대로 쓰지 말고 사용자 체감 관점으로 재작성.

| 나쁨 (기술 관점) | 좋음 (기능 관점) |
|------------------|------------------|
| feat: ViewModel에 공연장 탭 StateFlow 추가 | 공연 상세 화면에 공연장 탭 추가 |
| fix: NPE in ShowListScreen | 공연 목록에서 간헐적으로 앱이 종료되던 문제 해결 |
| refactor: Hilt 모듈 정리 | *(제외 — 사용자 체감 없음)* |

변환 원칙:
- 앱에서 보거나 누르거나 경험할 수 있는 것만 남긴다
- 화면·기능 이름은 앱 내 UI 문구에 맞춘다 ("공연 상세", "예매 내역", "내 티켓", "선물함")
- 동사는 사용자 관점 ("추가", "개선", "해결", "지원")
- 한 줄 = 한 가지 변경
- 티켓 번호(`Boolti-XXX`)는 QA에겐 노이즈이므로 제외

### 3-5. 템플릿

```
## v{versionName}

### 주요 업데이트
- {기능 위주 한 줄}

### 버그 수정
- {사용자가 겪던 문제와 해결 결과}
```

비어 있는 섹션은 제거. 항목이 0개면 "이번 빌드는 내부 개선 위주입니다" 로 사용자 재확인.

### 3-6. 사용자 확인 (blocking)

초안을 보여주고 명시적 승인을 받는다. 수정 요청 시 반영 후 재확인. OK 전에는 Step 4로 넘어가지 않는다.

## Step 4. 릴리즈 빌드

빌드 variant(`debug`/`release`)를 먼저 정한다. 각 variant는 전용 Firebase 앱에 매핑되며 sub-skill이 artifact 경로로 자동 구분한다. 기본은 **debug**(빠른 테스터 배포), 스토어 직전 QA처럼 release가 필요한 맥락이면 사용자에게 확인한다.

```bash
./gradlew clean
./gradlew assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease    # → app/build/outputs/apk/release/app-release.apk  (keystore.properties 필요)
```

빌드 산출물의 실제 경로를 Step 5로 넘긴다.

## Step 5. 배포 sub-skill 위임

**App Distribution 경로**: `boolti-app-distribution` 스킬을 호출하고 아래 입력을 전달한다.

| 입력 | 값 |
|------|----|
| `artifact` | Step 4에서 빌드된 APK/AAB 경로 |
| `release_notes` | Step 3에서 승인된 인라인 텍스트 |
| `groups` | sub-skill 기본값 사용 (사용자가 변경 요청하면 override) |

App ID는 sub-skill이 `artifact` 경로로 자동 결정한다. 오케스트레이터는 Step 4 variant와 빌드 경로가 일치하는지만 위임 직전에 재확인.

CLI 실행·검증·결과 보고는 sub-skill 책임. 본 스킬은 결과만 사용자에게 전달.

**Play Console 경로**: Step 0에서 종료됐어야 함. 여기 도달하면 오케스트레이션 버그.

## Step 6. (선택) 릴리즈 태깅

운영 릴리즈에서만. App Distribution 단독은 대개 생략.

```bash
git tag -a v{versionName} -m "Release v{versionName}"
git push origin v{versionName}
```

## 단독 호출 경로

- "이미 빌드된 APK만 테스터에게 배포해줘" → `boolti-app-distribution` 직접 호출
- "릴리즈 노트만 뽑아줘" → 본 스킬 Step 3만 실행
