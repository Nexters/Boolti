---
name: boolti-create-pr
description: 불티 프로젝트의 컨벤션에 맞춰 Pull Request를 생성한다. "PR 만들어줘", "develop에 PR 올려줘", "풀리퀘스트 생성해줘", "PR 올려줘", "이거 PR 보내줘" 등 PR 생성 요청에 트리거.
---

# 불티 PR 생성 스킬

불티 프로젝트의 Pull Request 생성 전 과정을 컨벤션에 맞춰 처리한다.
Pre-flight 검증 → 티켓 추출 → 변경 요약 → 제목/바디 생성 → 원격 동기화 → 리뷰어·레이블·마일스톤 결정 → PR 생성까지 한 번에.

## ⚠️ 원칙

- **절차를 건너뛰지 않는다.** Pre-flight 검증을 통과하기 전까지 `gh pr create`를 호출하지 않는다.
- **사용자 작업물을 임의로 수정하지 않는다.** 커밋이 필요하면 사용자에게 먼저 묻는다.
- **본문은 템플릿을 따른다.** 저장소의 `.github/pull_request_template.md` 구조를 존중한다.
- **제목은 `[Boolti-XXX] 제목` 형식.** 티켓 번호가 있으면 대괄호에 감싸 맨 앞에 붙이고 한 칸 띄운 뒤 요약을 쓴다. (예: `[Boolti-470] 공연장 탭 추가`)
- **이슈 트래커는 GitHub Issues 하나뿐.** `Boolti-XXX`는 이 저장소 GitHub Issue `#XXX`를 가리키는 내부 표기일 뿐이다. Jira나 다른 트래커 언급 금지.

## 워크플로우

아래 순서를 반드시 지킨다.

### Step 1. Pre-flight 검증 (blocking)

| 검증 | 명령 | 실패 시 |
|------|------|---------|
| 현재 브랜치가 `develop`/`main`이 아님 | `git branch --show-current` | 중단. 사용자에게 feature 브랜치로 이동하라고 안내 |
| 커밋이 존재 (base와 diff 있음) | `git log develop..HEAD --oneline` | 중단. 커밋할 내용이 있는지 확인 |
| 워킹 트리가 clean | `git status --porcelain` | 사용자에게 알리고 커밋/스태시 여부 확인 (임의 커밋 금지) |
| 동일 브랜치로 열린 PR이 없음 | `gh pr list --head <branch> --state open --json number` | 이미 있으면 URL 안내 후 중단. 업데이트는 `git push`만 하면 된다고 안내 |
| Quality Gate 통과 | `bash .claude/skills/boolti-feature-planner/scripts/quality-gate.sh --no-test` | 실패 항목 보고 후 중단. 사용자가 fix 요청하면 먼저 해결 |
| AppTracker 변경 검증 | 아래 "AppTracker 검증" 절 참고 | 스킬 실행 → 지적 사항 반영 → 재시도 |

Quality Gate 스크립트가 없거나 실행이 어려운 환경이면 최소한 `./gradlew assembleDebug --quiet`는 통과해야 한다.

#### AppTracker 검증

이번 PR의 커밋(`develop..HEAD`)에 **Mixpanel/AppTracker 관련 변경**이 있으면 `boolti-mixpanel-validator` 스킬을 먼저 실행해 컨벤션을 검증한다.

검사 방법:

```bash
# 1) 트래커 모듈 변경 여부
git diff develop...HEAD --name-only | grep -E '^common/tracker/'

# 2) AppTracker 호출 추가/수정 여부 (.kt/.kts)
git diff develop...HEAD -U0 -- '*.kt' '*.kts' \
  | grep -E '^\+[^+]' \
  | grep -E 'AppTracker\.|\btrackEvent\('
```

둘 중 하나라도 매치되면:
1. `Skill` 도구로 `boolti-mixpanel-validator` 호출, 해당 변경 파일을 검토.
2. 지적 사항이 있으면 수정 → 재커밋 후 PR 단계로 복귀.
3. 지적 사항이 없거나 이미 해소됐으면 "검증 완료" 메모와 함께 다음 단계로.

매치가 없으면 이 단계는 통과 처리.

> 커밋 시점에도 `PreToolUse` 훅(`.claude/hooks/check-apptracker-before-commit.sh`)이 같은 검사를 수행한다. PR 단계의 이 절차는 커밋이 훅 밖에서 이뤄졌을 가능성을 커버하는 **2차 방어선**이다.

### Step 2. 브랜치·티켓·타입 추출

현재 브랜치명을 파싱한다.

```bash
git branch --show-current
```

**브랜치 카테고리 → 레이블 매핑** (카테고리는 슬래시 앞부분):

| 카테고리 | 레이블 |
|---------|--------|
| `feature` | `feat` |
| `fix` / `hotfix` | `bug` |
| `refactor` | `refactor` |
| `chore` | `chore` |
| `style` | `style` |
| `enhance` / `enhancement` | `enhancement` |
| `docs` | `documentation` |
| `qa` | 변경 성격에 맞는 레이블 (`bug`, `refactor`, `style` …) |
| `release` | `chore` (또는 무레이블) |

제목에는 카테고리가 드러나지 않는다 (형식은 `[Boolti-XXX] 요약`뿐).

**슬래시 뒷부분(슬러그)에서 이슈 번호 추출**:

다음 패턴을 순서대로 시도해 첫 매칭된 숫자를 이슈 번호로 채택한다.

1. `Boolti-<숫자>` — 예: `feature/Boolti-470` → `470`
2. `Boolti-<숫자>-<suffix>` — 예: `feature/Boolti-444-textfield` → `444`
3. 카테고리 바로 뒤의 순수 숫자 — 예: `enhance/463` → `463`
4. 슬러그 어디에든 있는 첫 `\d+` — 예: `qa/pre-questions-2` → `2` (주의: 의미 없는 숫자일 수 있으므로 항상 사용자에게 확인)

**번호가 없는 케이스** (예: `qa/search-navigation`, `qa/prequestion-spec-change`, `release/1.13.0`):
- GitHub 이슈가 있는지 `AskUserQuestion`으로 사용자에게 묻는다.
- 이슈 번호를 받으면 제목은 `[Boolti-<번호>] 요약`, 바디는 `Closes #<번호>`.
- 이슈가 없으면 제목은 대괄호 없이 요약만 쓰고, 바디의 `Issue` 섹션은 생략한다.

**release 브랜치 특수 처리** (`release/x.y.z`):
- 제목은 `[Boolti-<번호>] <version> 릴리즈` (릴리즈 티켓이 있는 경우) 또는 `<version> 릴리즈`.
- base가 `main`일 가능성이 높다. 반드시 사용자에게 base 브랜치를 확인한다 (기본 `develop` 그대로 가면 안 됨).

**카테고리가 모호하거나 매핑 불가** (예: 브랜치가 `mangbaam/test` 같은 임시 이름):
- 커밋 메시지와 변경 파일로 레이블을 추정.
- 그래도 모호하면 `AskUserQuestion`으로 확인.

### Step 3. 변경 내용 요약

다음 정보를 모아 PR 바디를 작성한다.

```bash
git log develop..HEAD --pretty=format:'%s' --no-merges    # 커밋 메시지
git diff develop...HEAD --stat                             # 파일 변경량
git diff develop...HEAD --name-only                        # 변경 파일 목록
```

**요약 규칙** (핵심은 **짧게 쓰기**):
- `작업 내용`은 **최대 3~4개 bullet**, 각 bullet은 **한 줄**. 장황한 서술·여러 절·긴 예시 금지.
- 커밋 1:1 매핑 금지. 의미 있는 단위로 묶는다.
- "무엇을 했는지"보다 **결과/변화**를 드러낸다. ("ReservationRepository 추가" → "예약 목록 API 연동")
- 모듈명이나 파일 경로를 줄줄이 나열하지 않는다. 그 정보는 diff가 이미 말해준다.
- **강조할 만한 지점이 있을 때만** `리뷰 포인트` 섹션을 추가한다. (없으면 생략)
  - 예: 로직 변경이 큰 부분, 호환성 영향, stub/mock, TODO, 서버 배포 선행 필요, 의도적 예외 처리 등
  - 여기도 불릿 1~3개로 간결하게.
- API·Repository가 아직 미완성이면 `리뷰 포인트`에 stub 상태를 반드시 남긴다.

### Step 4. 제목 생성

**형식** (컨벤션, 예외 없음): `[Boolti-<번호>] <간결한 한국어 요약>`

규칙:
- 티켓 번호가 있으면 반드시 `[Boolti-XXX]` 대괄호 prefix를 붙인다. `[` 와 번호 사이·`]` 와 제목 사이 공백 형식을 지킨다.
- 요약부(대괄호 뒤)는 **60자 이하** 권장. 전체 길이는 70자를 넘기지 않는다.
- 모호한 동사("수정", "변경") 지양, 핵심 결과("공연장 탭 추가", "사전 질문 UI 개선")를 드러낸다.
- 요약에는 `feat:` / `fix:` / `refactor:` 같은 prefix를 **붙이지 않는다** (대괄호 티켓 번호만 사용).
- 여러 영역이 섞여 있으면 가장 큰 변화를 대표로 삼고 부차 변화는 바디로.
- 티켓 번호가 없는 경우(Step 2 참고)에만 대괄호 없이 요약만 쓴다.

예시:
- `[Boolti-470] 공연장 찾기 배너 및 검색 결과 공연장 탭 추가`
- `[Boolti-467] 다이얼로그 제목 크기 변경`
- `[Boolti-451] 사전 질문 API 스펙 변경 대응`
- `[Boolti-434] ShowItemV2 디자인 적용`

### Step 5. 바디 생성

저장소 템플릿(`.github/pull_request_template.md`)을 기준으로 **간결하게** 쓴다.
기본 구조 (리뷰 포인트는 필요할 때만):

```markdown
## Issue
- Closes #<티켓 숫자>

## 작업 내용
- <한 줄 요약 bullet 1>
- <한 줄 요약 bullet 2>
- <한 줄 요약 bullet 3>

## 리뷰 포인트  (선택, 강조할 게 있을 때만)
- <특히 봐야 할 변경 1>
- <위험 요소 / stub / TODO / 호환성 메모>

<img src="" width="300" />
```

**세부 규칙**:
- **장황함 금지**. 바디는 PR 훑는 리뷰어가 10초 안에 핵심을 파악할 수 있을 만큼 짧아야 한다.
- `Issue`: `Closes #<num>` — 불티는 GitHub Issues만 쓴다. 연결할 이슈가 없으면 섹션 자체를 생략.
- `작업 내용`:
  - 최대 3~4개 bullet, 각 bullet 한 줄.
  - 메타 서술("~을 위해 ~을 추가하여 ~했습니다")로 늘리지 말고 결과만 쓴다.
  - 여러 변경을 한 bullet에 억지로 끼워 넣지 말고, 정말 의미 있는 단위만 남긴다.
- `리뷰 포인트` (있을 때만 추가):
  - 다음 중 하나라도 해당되면 이 섹션을 둔다. 해당 없으면 섹션 자체를 생략.
    - Stub / mock / 미완성 API
    - 후속 PR로 분리된 작업
    - 동작 전제 (서버 배포 선행 등)
    - 로직/아키텍처 영향이 큰 변경, 리뷰어가 주의 깊게 봐야 할 부분
    - 알려진 제약 / known issue
  - 최대 3개 bullet. 배경 설명은 1~2문장 이내.
- **스크린샷/영상**:
  - UI 변경이 감지되면 (예: `presentation/**`의 `.kt` 변경) 사용자에게 스크린샷·영상 첨부를 요청한다.
  - 사용자가 이미 이미지/영상 URL을 제공했다면 본문에 삽입. 없으면 빈 `<img src="" width="300" />`를 남겨 나중에 채울 수 있게 한다.
  - Android UI는 세로 스크린샷이 많으므로 `width="300"` 기본 유지.
  - UI 변경이 없으면 `<img>` 자리는 생략해도 된다.

### Step 6. 원격 브랜치 동기화

```bash
git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null
```

- upstream이 없으면 `git push -u origin <branch>` 실행 (사용자 확인 후).
- upstream은 있지만 미푸시 커밋이 있으면 `git push` 실행 (확인 후).

### Step 7. PR 메타데이터 결정

#### 7a. 리뷰어·담당자·레이블·draft

리뷰어는 현재 Git user를 제외한 나머지 한 명이다.

```bash
gh api user -q '.login'
```

리뷰어 풀 `{mangbaam, HamBP}`에서 현재 사용자를 빼고 남은 한 명을 지정. 현재 사용자가 풀에 없으면 (예: 새 팀원이 push) `mangbaam`과 `HamBP`를 **둘 다** 리뷰어로 지정한다.

| 항목 | 기본값 |
|------|--------|
| base | `develop` (release 브랜치는 `main` 가능성 있음 — 확인) |
| reviewer | `mangbaam`/`HamBP` 중 현재 Git user를 제외한 사람 |
| assignee | 현재 Git user |
| label | Step 2의 매핑 |
| draft | **기본 false**. 사용자가 draft를 명시했거나 커밋/작업 내용에 WIP 표시가 있을 때만 `--draft` |

커밋 메시지에 `WIP`가 있거나 사용자가 "초안/임시/draft"를 언급하면 `WIP` 레이블과 `--draft` 둘 다 적용.

#### 7b. 마일스톤 (필수)

모든 PR에 **마일스톤을 반드시 지정**한다.

**정책**:
- **앱 배포에 포함되는 변경** → 현재 작업 중인 **앱 버전 마일스톤** (예: `1.15.0`).
- **앱 배포에 포함되지 않는 변경** (Claude 스킬·커맨드·플러그인·스크립트·CI·문서 등) → **`Tools` 마일스톤**.
- **폐기 마일스톤**: `gift`, `ticketing`, `login`, `payment` 등 주제/영역 기반의 기존 마일스톤은 모두 폐기됐다. 후보로 제시하지 않는다.

**실행 순서**:

1. 열린 마일스톤 조회:
   ```bash
   gh api "repos/:owner/:repo/milestones?state=open" -q '.[] | {number,title}'
   ```
2. Step 3에서 수집한 변경 파일 목록으로 default 마일스톤 추정:
   - `.claude/**`, `docs/**`, `scripts/**`, `.github/**`, 루트 md 파일에만 한정 → `Tools`
   - 그 외 (`app/`, `presentation/`, `domain/`, `data/`, `common/`, `tosspayments/` 등) → 현재 앱 버전 마일스톤
3. `AskUserQuestion`으로 마일스톤을 묻는다. 열린 마일스톤 목록을 옵션으로 제시하되, 2의 추정 결과를 default로.
4. 사용자가 고른 제목을 `--milestone "<title>"`로 PR 생성 시 전달.

#### 7c. 사용자 미리보기 및 승인 (blocking)

`gh pr create` 실행 **전에** 아래 포맷으로 사용자에게 미리보기를 보여주고 승인을 받는다. 원시 마크다운을 코드 블록에 가두지 않고, 채팅 렌더링이 되도록 구조화한다.

**포맷 규칙**:
- 상단·하단을 `═` 구분선으로 감싸 미리보기 영역을 시각적으로 분리
- 섹션 헤더는 **Bold** + 아이콘 마커(`■` 상위, `▌` 하위)로 표현
  - `■ 제목`, `■ 메타` — 최상위 섹션
  - `▌ Issue`, `▌ 작업 내용`, `▌ 스킬 사용 가이드`, `▌ 리뷰 포인트` — 바디 내부 섹션
  - 실제 PR 바디에는 `## Issue` 등 마크다운 헤더가 들어가지만, 터미널에서 `##`는 시각적으로 튀지 않으므로 미리보기에선 아이콘 마커로 치환
- 메타는 `| 항목 | 값 |` 표로, 바디 헤더와의 구분은 `─` 가로선으로
- 표로 요약 가능한 섹션(스킬 사용 가이드 등)은 표 형식 유지
- 긴 인라인 코드는 `` ` ``로, 경로/값 강조는 `` ` `` 사용
- UI 변경이 있으면 스크린샷 placeholder(`<img src="" width="300" />`)도 미리보기 하단에 표기

**템플릿**:

```
═══════════════════ PR 미리보기 ═══════════════════
```

**■ 제목**
&nbsp;&nbsp;`[Boolti-XXX] <요약>`

**■ 메타**

| 항목 | 값 |
|------|----|
| base | `develop` |
| reviewer | `...` |
| assignee | `...` |
| label | `...` |
| milestone | `...` |

```
─────────────────── 바디 ────────────────────
```

**▌ Issue**
- Closes #XXX

**▌ 작업 내용**
- ...

**▌ 리뷰 포인트** *(있을 때만)*
- ...

```
══════════════════════════════════════════════════
```

미리보기를 보여준 뒤 **명시적 승인**을 받는다. "이대로 진행", "좋아", "OK" 같은 명시적 OK 전에는 Step 8로 넘어가지 않는다.

수정 요청이 들어오면 반영 후 **미리보기 전체를 다시** 출력해 재확인. 부분만 조각내서 보여주지 않는다.

> **왜 이렇게?** 채팅 화면에서 `#`/`##` 마크다운 헤더는 렌더링돼도 시각적으로 크게 튀지 않아 섹션 경계가 흐려진다. 아이콘 마커(`■`, `▌`) + 가로 구분선(`═`, `─`)은 monospace 렌더링에서도 계층이 또렷이 보이므로 사용자가 한눈에 구조를 파악할 수 있다.

### Step 8. PR 생성

HEREDOC으로 본문을 전달해 포맷 보존한다. Step 7에서 결정한 reviewer/assignee/label/milestone을 모두 옵션으로 전달한다.

```bash
gh pr create \
  --base develop \
  --reviewer <7a에서 선정> \
  --assignee <현재 Git user> \
  --label <매핑된 레이블> \
  --milestone "<7b에서 선택한 마일스톤>" \
  --title "[Boolti-<번호>] <요약>" \
  --body "$(cat <<'EOF'
## Issue
- Closes #<번호>

## 작업 내용
- ...

## 리뷰 포인트
- ...

<img src="" width="300" />
EOF
)"
```

옵션 조건부 처리:
- `--draft`: 7a의 판단에 따라 추가
- `--milestone`: **항상 지정** (정책). 마일스톤 없이 PR을 만들지 않는다
- 티켓 번호가 없는 경우는 대괄호 없이 요약만 제목으로 사용

### Step 9. 후처리

- 생성된 PR URL을 사용자에게 알린다.
- UI 변경이 있었다면 스크린샷 첨부를 재차 안내.
- `리뷰 포인트`에 stub/TODO가 있었다면 후속 작업을 언급.
- CI(PR checks) 상태는 **폴링하지 않는다**. 사용자가 요청할 때만 `gh pr checks <번호>` 실행.

## 실수 방지 체크리스트

- [ ] **바디가 짧은가.** `작업 내용`은 한 줄 bullet 3~4개 이내, 리뷰어가 10초 만에 핵심을 파악할 수 있는 분량
- [ ] 강조할 게 있을 때만 `리뷰 포인트` 섹션 사용. 할 말 없으면 **섹션 자체를 생략**
- [ ] 제목은 `[Boolti-XXX] 요약` 형식. `feat:`/`fix:` 같은 conventional prefix **금지**
- [ ] 바디의 이슈 연결은 `Closes #<숫자>` 표준 사용
- [ ] HEREDOC 없이 `--body "..."`만 쓰다가 줄바꿈 깨뜨리지 않기
- [ ] 레이블 오타 금지 (`feat`, `bug`, `refactor`, `chore`, `style`, `enhancement`, `documentation`, `WIP` — 이 집합에만 존재)
- [ ] 리뷰어는 `mangbaam`/`HamBP` 중 **현재 Git user를 제외한 사람** 자동 지정
- [ ] **마일스톤 필수 지정**. 사용자에게 물어서 현재 앱 버전 마일스톤(예: `1.15.0`) 또는 `Tools` 마일스톤을 고른다
- [ ] 폐기된 마일스톤(`gift`, `ticketing`, `login` 등 영역/주제 기반)은 사용도 추천도 하지 않기
- [ ] `--base main` 사용 금지 (항상 `develop`). main으로 올려야 하는 릴리즈 PR은 별도 요청 시에만
- [ ] **Step 7c 미리보기 승인** 없이 `gh pr create` 실행 금지. 아이콘 마커(`■`, `▌`) + 구분선 포맷을 지킨다
- [ ] 사용자 승인 없이 임의로 `git commit` / `git push --force` 실행 금지
- [ ] 이미 열려 있는 PR에 덮어쓰려 하지 말 것 (push만으로 갱신됨)
- [ ] 실패한 Quality Gate를 무시하고 PR 올리지 말 것
- [ ] Jira/다른 트래커 언급 금지 — 이슈 트래커는 GitHub Issues 하나뿐

## 예외 및 엣지 케이스

- **브랜치에 티켓 번호가 없음** (예: `qa/search-navigation`, `feature/add-logger`): GitHub 이슈 번호가 있는지 사용자에게 확인. 있으면 `[Boolti-<번호>] 요약`, 없으면 대괄호 없이 요약만.
- **카테고리 뒤가 순수 숫자** (예: `enhance/463`): `Boolti-` 없어도 그 숫자를 이슈 번호로 사용 → `[Boolti-463] ...`.
- **슬래시 뒤에 `-suffix`가 붙은 경우** (예: `feature/Boolti-444-textfield`, `feature/Boolti-422-navigation3`): `Boolti-` 다음의 첫 숫자 그룹만 이슈 번호, suffix는 무시.
- **동일 티켓으로 쪼개진 후속 브랜치** (예: `feature/Boolti-405-api`, `feature/Boolti-405-2`): 같은 이슈 번호를 쓰되, 이미 그 이슈로 merge된 PR이 있는지 `gh pr list --search "Boolti-405"` 로 확인해 본 다음 후속 PR임을 바디에 명시.
- **QA 브랜치** (`qa/*`): 한 번에 여러 QA 수정이 묶이는 경우가 많다. 티켓 번호가 있으면 `[Boolti-XXX] QA 이슈 대응`, 작업 내용 섹션에 항목별 bullet 나열.
- **여러 변경 타입이 섞임** (예: feat + refactor 동시): 레이블은 주된 것 하나만. 바디에서 보조 변화 설명.
- **릴리즈/버전업 PR** (`release/x.y.z`): 제목 `[Boolti-<번호>] <version> 릴리즈` (릴리즈 티켓이 있을 때) 또는 `<version> 릴리즈`. 레이블 `chore`. 마일스톤은 해당 버전. **base는 `main`일 가능성이 높으므로 반드시 사용자에게 확인**한 뒤 `--base` 옵션을 결정.
- **앱 배포에 포함되지 않는 작업** (Claude 스킬·커맨드·플러그인·스크립트·CI·문서): 제목은 동일 규칙. 마일스톤은 `Tools`.
- **도메인/데이터만 변경, UI 없음**: 스크린샷 섹션 생략. `리뷰 포인트`에 영향 범위와 호환성 명시.
- **revert PR**: 제목 `[Boolti-<번호>] <원 PR 요약> revert`. 바디에 원 PR 링크 필수.

## 참고

- PR 템플릿: [.github/pull_request_template.md](../../../.github/pull_request_template.md)
- Quality Gate 스크립트: `.claude/skills/boolti-feature-planner/scripts/quality-gate.sh`
- 사용 가능한 레이블: `gh label list`로 확인
