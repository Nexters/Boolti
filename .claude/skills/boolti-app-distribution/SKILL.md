---
name: boolti-app-distribution
description: 이미 빌드된 불티 Android APK/AAB를 Firebase App Distribution으로 업로드한다. 오케스트레이터 `boolti-release`에서 호출되거나, "이 APK 테스터한테 배포해줘", "빌드된 거 App Distribution에 올려줘", "Firebase App Distribution 배포" 같은 요청에 단독 트리거. 빌드·릴리즈 노트 생성은 수행하지 않고, 인라인으로 받은 릴리즈 노트를 그대로 `--release-notes`로 전달한다.
---

# Firebase App Distribution 업로드 스킬

**단일 책임**: 이미 빌드된 아티팩트(APK/AAB)를 Firebase App Distribution에 업로드. 빌드·릴리즈 노트 작성·버전 관리는 다루지 않는다.

## 기본값

`CLAUDE.md`의 Firebase Integration 섹션이 source of truth. 요약:

| 항목 | 값 |
|------|----|
| Firebase Project | `boolti-9a521` |
| `groups` | `안드폰-사용자들` |
| **Debug App ID** | `1:965765235527:android:fd491b0a5869fc69d30262` (package `com.nexters.boolti.debug`) |
| **Release App ID** | `1:965765235527:android:6ed43e2462526efbd30262` (package `com.nexters.boolti`) |

### App ID 자동 결정 규칙 (핵심)

불티는 debug/release 두 Firebase 앱이 분리되어 있다. **잘못된 앱에 올리면 패키지 불일치로 설치 실패 또는 오배포**가 발생한다. 호출자가 `app_id`를 명시하지 않으면 `artifact` 경로로 자동 추론한다.

| artifact 경로 패턴 | 사용할 App ID |
|-------------------|---------------|
| `apk/debug/**/*.apk` 또는 경로에 `-debug` 포함 | **Debug** App ID |
| `apk/release/**/*.apk`, `bundle/release/**/*.aab` 또는 경로에 `-release` 포함 | **Release** App ID |
| 그 외 (커스텀 경로 등) | **사용자에게 확인** (blocking) |

추론 후에도 확신이 없으면 사용자에게 "이 APK는 debug / release 중 어느 쪽인가요?" 로 묻고, 확인을 받은 뒤 진행한다.

## 입력

호출자(오케스트레이터 또는 사용자)가 다음을 제공. 누락 시 **한 번에 모아서** 사용자에게 묻는다.

| 이름 | 필수 | 비고 |
|------|------|------|
| `artifact` | 필수 | APK/AAB 파일 경로. 존재 여부·확장자(apk/aab) 확인 |
| `release_notes` | 필수 | 인라인 텍스트. 파일 경로 아님. 빈 문자열 불허 |
| `groups` | 선택 | 쉼표 구분. 미지정 시 기본값 |
| `testers` | 선택 | 개별 이메일, 쉼표 구분 |
| `app_id` | 선택 | 미지정 시 `artifact` 경로로 **자동 추론** (위 표 참고). 커스텀 앱 타깃일 때만 명시 |

## 사전 조건

- `firebase --version`으로 CLI 존재 확인. 없으면 `npm install -g firebase-tools` 안내.
- 인증 중 하나:
  - 로컬: `firebase login`
  - CI: `GOOGLE_APPLICATION_CREDENTIALS`에 서비스 계정 JSON 경로

## 실행

```bash
firebase appdistribution:distribute "$artifact" \
  --app "$app_id" \
  --groups "$groups" \
  --release-notes "$release_notes"
```

- `release_notes`는 `$(cat <<'EOF' ... EOF)` 헤레도크로 전달해 `$`·백틱 해석을 억제한다.
- `testers`가 있으면 `--testers "$testers"` 추가 (groups와 병행 가능).
- 실제로 쓰는 플래그는 `--app`, `--groups`, `--testers`, `--release-notes` 4개. 전체 플래그는 [공식 CLI 문서](https://firebase.google.com/docs/app-distribution/android/distribute-cli) 참고.

> `--release-notes-file` 은 쓰지 않는다 (파일 방식 폐기).

## 결과 보고

- 성공: 업로드 완료, Firebase Console 링크, 전달된 그룹/이메일 요약
- 실패: CLI stderr를 그대로 사용자에게 전달. 에러 메시지 자체가 원인을 설명하므로 별도 해석 불필요.
