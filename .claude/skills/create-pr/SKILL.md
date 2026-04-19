---
name: create-pr
description: 이 스킬은 사용자가 "PR 만들어줘", "develop에 PR 올려줘", "풀리퀘스트 생성해줘", "PR 올려줘" 등 PR 생성을 요청할 때 사용한다.
---

# 불티 PR 생성 스킬

불티 프로젝트의 PR 생성 규칙에 맞춰 Pull Request를 생성한다.

## PR 생성 절차

### 1. 빌드 확인

PR 생성 전 빌드가 정상인지 확인한다.

```bash
./gradlew assembleDebug
```

빌드 실패 시 PR 생성을 중단하고 사용자에게 알린다.

### 2. 브랜치 및 티켓 정보 확인

현재 브랜치에서 티켓 번호를 추출한다.

```bash
git branch --show-current
```

브랜치 명명 규칙: `feature/Boolti-XXX`, `fix/Boolti-XXX` 등에서 `Boolti-XXX` 추출.

### 3. 커밋 내용 분석

base 브랜치(develop)와의 차이를 분석하여 PR 내용을 파악한다.

```bash
git log develop..HEAD --oneline
git diff develop...HEAD --stat
```

### 4. PR 제목 작성

형식: `[티켓번호] 제목`

예시:
- `[Boolti-434] ShowItemV2 디자인 적용`
- `[Boolti-123] 로그인 버그 수정`

### 5. PR 본문 작성

템플릿 형식:

```markdown
## Issue
- close #이슈번호

## 작업 내용
- 변경 사항 1
- 변경 사항 2

<img src="" width="300" />
```

- Issue 섹션: 관련 GitHub 이슈 번호 연결 (티켓 번호와 동일할 수 있음)
- 작업 내용: 커밋 메시지를 기반으로 주요 변경 사항 요약
- 스크린샷: UI 변경이 있는 경우 사용자에게 스크린샷 추가 안내

### 6. PR 생성

```bash
gh pr create \
  --base develop \
  --draft \
  --reviewer HamBP \
  --assignee mangbaam \
  --title "[티켓번호] 제목" \
  --body "PR 본문"
```

필수 옵션:
- `--base develop`: 타겟 브랜치
- `--draft`: Draft PR로 생성
- `--reviewer HamBP`: 리뷰어 지정
- `--assignee mangbaam`: 담당자 지정

### 7. 완료 안내

PR 생성 후 URL을 사용자에게 알린다.

UI 변경이 포함된 경우, 스크린샷 추가를 안내한다.

## 주의사항

- 빌드 실패 시 PR을 생성하지 않는다
- 커밋이 없는 경우 PR을 생성하지 않는다
- 이미 PR이 존재하는 경우 사용자에게 알린다
