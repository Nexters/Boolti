---
paths:
  - "**/*.gradle.kts"
  - "gradle/**"
  - "build-logic/**"
---

# 빌드 설정

## Git에 없는 파일

- `local.properties` - API 키, 환경 설정. 실제 값은 팀 비공개 채널에서 받는다
  ```
  KAKAO_APP_KEY="<카카오 앱 키>"
  DEV_BASE_URL="<개발 API base URL>"
  PROD_BASE_URL="<운영 API base URL>"
  DEV_DOMAIN="<개발 도메인>"
  PROD_DOMAIN="<운영 도메인>"
  DEV_TOSS_CLIENT_KEY="<개발 토스 클라이언트 키>"
  DEV_TOSS_SECRET_KEY="<개발 토스 시크릿 키>"
  PROD_TOSS_CLIENT_KEY="<운영 토스 클라이언트 키>"
  PROD_TOSS_SECRET_KEY="<운영 토스 시크릿 키>"
  DEV_MIXPANEL_TOKEN="<개발 Mixpanel 토큰>"
  PROD_MIXPANEL_TOKEN="<운영 Mixpanel 토큰>"
  YOUTUBE_API_KEY="<YouTube Data API 키>"
  DISCORD_DEBUG_INFO_WEBHOOK_URL="<디버그 정보 디스코드 웹훅>"   # 선택. 없으면 전송 버튼 비활성화
  FIGMA_TOKEN="<Figma personal access token>"   # 로컬 스킬용
  ```
- `keystore.properties` - 릴리즈 서명 설정
- `app/google-services.json` - Firebase 설정

## 빌드 특징

- Android 모듈은 `build-logic/`의 convention plugin을 적용한다
  - 앱: `boolti.android.application`, 라이브러리: `boolti.android.library`
  - compileSdk·minSdk·targetSdk·Java 버전은 플러그인이 `libs.versions.toml` 값으로 설정한다. 모듈에서 다시 쓰지 않는다
- `local.properties` 값은 `localProperty(key)`로 읽어 `BuildConfig`에 넣는다. 디버그는 `DEV_*`, 릴리즈는 `PROD_*`를 쓴다
  - Gradle property(`-P`, `gradle.properties`)가 있으면 그 값을 먼저 쓴다
- APK 이름: `app-<buildType>-<versionName>-<gitHash>-<yyyyMMddHHmmss>.apk`
- 릴리즈 빌드는 R8(`isMinifyEnabled = true`)이 켜져 있다. 규칙은 `app/proguard-rules.pro`
