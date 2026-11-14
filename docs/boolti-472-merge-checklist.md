# Boolti-472 GitHub Actions 개선 머지 체크리스트

`feature/Boolti-472` 통합 브랜치에 쌓인 6개 서브 PR을 추적하고, 모두 머지된 뒤 수동으로 수행할 후속 작업을 정리한다.

## 6개 서브 PR

| # | PR | 요약 |
|---|----|------|
| 1 | [#482](https://github.com/Nexters/Boolti/pull/482) | [Boolti-477] GitHub Actions Gradle 빌드 캐시 추가 |
| 2 | [#484](https://github.com/Nexters/Boolti/pull/484) | [Boolti-473] PR 마일스톤 필수 액션 추가 |
| 3 | [#485](https://github.com/Nexters/Boolti/pull/485) | [Boolti-481] Secret 유출 방지 TruffleHog 스캔 추가 |
| 4 | [#487](https://github.com/Nexters/Boolti/pull/487) | [Boolti-486] APK 사이즈 diff 댓글 워크플로우 추가 |
| 5 | [#488](https://github.com/Nexters/Boolti/pull/488) | [Boolti-475] data 레이어 runCatching 안티패턴 검증 액션 추가 |
| 6 | [#489](https://github.com/Nexters/Boolti/pull/489) | [Boolti-480] 릴리즈 버전 검증 액션 추가 |

## 머지 이후 수동 작업

### 1. Develop 브랜치 보호 규칙 설정 (Boolti-474)

Settings → Branches → Add branch protection rule (`develop`):

- [ ] Require a pull request before merging
- [ ] Require approvals: **1**
- [ ] Dismiss stale pull request approvals when new commits are pushed (선택)
- [ ] Require status checks to pass before merging
  - 필수 지정 후보 (본 PR이 한 번 머지·실행된 뒤 이름이 목록에 등장):
    - [ ] `unit_test` — `pull-request-ci.yml`
    - [ ] `check-milestone` — `pr-milestone-required.yml`
    - [ ] `scan` — `secret-scan.yml`
  - 선택 등록 (paths 필터라 PR마다 실행 여부 다름):
    - [ ] `run-catching` — `anti-pattern-check.yml`

### 2. Main 브랜치 보호 규칙

Settings → Branches → Add rule (`main`):

- [ ] 기본 보호 설정(PR 필수, approvals 1)
- [ ] Require status checks:
  - [ ] `check` — `release-version-check.yml` (release/* PR에서만 실행)

### 3. 통합 PR 생성·머지

- [ ] 본 체크리스트 md 파일 제거 커밋
- [ ] `feature/Boolti-472` → `develop` PR 생성
- [ ] 리뷰·머지 후 Boolti-472 이슈 닫기

## 참고: 닫힌 서브 이슈

- **#478** — #487(Boolti-486)에 APK 빌드 스텝이 포함되어 superseded
- **#479** — GitHub PR 페이지에서 변경 라인 수를 이미 확인할 수 있어 불필요
- **#476** — 로컬 pre-commit 훅 + PR 생성 스킬의 2차 방어선으로 충분, CI regex/LLM ROI 낮음
