---
name: boolti-release-finish
description: 출시가 끝난 불티 정기 릴리즈를 마무리한다. release/* PR을 main에 머지하고, v<버전> 태그와 GitHub Release를 만들고, develop 역머지 PR에 자동 머지를 예약한다. "릴리즈 마무리", "스토어 출시 끝났어", "릴리즈 태깅해줘", "main에 머지하고 태그 달아줘" 같은 요청에 트리거. 핫픽스 마무리는 boolti-hotfix-finish를 쓴다.
---

# 릴리즈 마무리

`.github/scripts/release/finish.sh release`를 실행하는 스킬이에요. 판단·실행 로직은 전부 스크립트에 있고, 스킬은 미리보기 → 승인 → 실행만 해요. 흐름 전체는 `.github/scripts/release/README.md`를 봐요.

## 순서

1. 미리보기를 실행하고 출력 그대로 보여줘요
   ```bash
   .github/scripts/release/finish.sh release
   ```
   - 사용자가 버전을 말했으면 `release` 뒤에 붙여요 (예: `finish.sh release 1.15.0`)
2. 사용자 승인을 받아요. 원격 브랜치·PR·태그를 바꾸는 작업이라 승인 없이 넘어가지 않아요
3. 같은 명령에 `--apply`를 붙여 실행해요
4. 출력에 나온 PR·Release 링크와 다음 할 일을 알려줘요

## Release 노트 다듬기 (선택)

스크립트는 PR 레이블로 Release 노트를 자동 생성해요. 사용자가 원하면 `gh release view v<버전>`으로 읽고, 앱 사용자 관점 문장으로 바꿔 `gh release edit v<버전> --notes-file <파일>`로 고쳐요. 바꾸기 전에 초안을 보여주고 승인을 받아요.

## 실패했을 때

- 스크립트가 `❌`로 멈추면 메시지를 그대로 전하고, 메시지가 시키는 일을 안내해요
- 스크립트를 우회해서 git·gh 명령을 직접 실행하지 않아요. 검사(버전 형식, 커밋 수, main 포함 여부)를 건너뛰게 돼요
