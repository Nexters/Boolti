# 릴리즈·핫픽스 스크립트

AI 없이도 쓸 수 있어요. 스킬(`boolti-release-*`, `boolti-hotfix-*`)과 Actions의 **Release** 버튼도 이 스크립트를 실행해요.

## 흐름

| 종류 | 기준 | 버전 | 브랜치 |
|---|---|---|---|
| `release` | `develop` | minor + 1 | `release/1.15.0` |
| `hotfix` | `main` | patch + 1 | `hotfix/1.14.4` |

```
start  → <kind>/<버전> 브랜치 + 버전 커밋 + main 대상 PR (QA 수정은 이 브랜치로 PR)
finish → main 머지 → 태그 v<버전> → GitHub Release → backmerge/<버전> → develop PR (자동 머지 예약)
cancel → 버전 커밋 하나만 있을 때 PR 닫기 + 브랜치·빈 마일스톤 삭제
```

- versionCode는 `app/app-version.gradle`이 versionName에서 계산해요 (`major * 100000 + minor * 100 + patch`)
- 실행 권한은 GitHub 브랜치 보호·Ruleset이 막아요. 권한이 없으면 push·머지 단계에서 GitHub이 거절해요

## 준비 (처음 한 번)

```bash
brew install gh jq
gh auth login
```

Windows는 Git Bash에서 실행해요.

디스코드 알림을 받으려면 `local.properties`에 웹훅 주소를 한 줄 추가해요. 주소는 팀 비공개 채널에서 받아요.

```properties
DISCORD_RELEASE_WEBHOOK_URL="https://discord.com/api/webhooks/..."
```

## 명령

`--apply`를 빼면 미리보기만 하고 아무것도 바꾸지 않아요.

| 하고 싶은 일 | 명령 |
|---|---|
| 릴리즈 시작 | `.github/scripts/release/start.sh release --apply` |
| 핫픽스 시작 | `.github/scripts/release/start.sh hotfix --apply` |
| 출시 후 마무리 | `.github/scripts/release/finish.sh release --apply` |
| 잘못 시작해서 취소 | `.github/scripts/release/cancel.sh release --apply` |

- 핫픽스는 `release` 자리에 `hotfix`를 넣어요
- 대상 브랜치가 여러 개면 버전을 같이 넘겨요: `finish.sh release 1.15.0 --apply`
- finish는 중간에 실패해도 다시 실행하면 이어서 진행해요
- 릴리즈 중에 핫픽스가 먼저 나가면 finish가 멈춰요. main을 릴리즈 브랜치에 머지해 PR에 올리고(versionName 충돌은 릴리즈 버전 유지), CI·리뷰 뒤에 다시 실행해요
- finish는 지금 마일스톤을 닫고, 다음 릴리즈 마일스톤이 없으면 만들어요

## 버튼으로 실행

GitHub → Actions → **Release** → Run workflow → `kind`, `action`을 골라요. 폰 GitHub 앱에서도 돼요.

## 스크립트를 고쳤다면

```bash
.github/scripts/release/test.sh
```

임시 저장소와 가짜 `gh`로 시나리오를 돌려요. 실제 GitHub은 건드리지 않아요.
