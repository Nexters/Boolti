---
name: boolti-hotfix-start
description: 불티 핫픽스를 시작한다. main에서 hotfix/<다음 patch 버전> 브랜치를 만들고 versionName을 올린 커밋과 main 대상 PR을 만든다. "핫픽스 시작", "운영 긴급 수정", "hotfix 브랜치 따줘", "크래시 핫픽스 나가야 해" 같은 요청에 트리거. 정기 릴리즈는 boolti-release-start를 쓴다.
---

# 핫픽스 시작

`.github/scripts/release/start.sh hotfix`를 실행하는 스킬이에요. 판단·실행 로직은 전부 스크립트에 있고, 스킬은 미리보기 → 승인 → 실행만 해요. 흐름 전체는 `.github/scripts/release/README.md`를 봐요.

## 순서

1. 미리보기를 실행하고 출력 그대로 보여줘요
   ```bash
   .github/scripts/release/start.sh hotfix
   ```
2. 사용자 승인을 받아요. 원격 브랜치·PR·태그를 바꾸는 작업이라 승인 없이 넘어가지 않아요
3. 같은 명령에 `--apply`를 붙여 실행해요
4. 출력에 나온 PR 링크와 다음 할 일을 알려줘요

## 실패했을 때

- 스크립트가 `❌`로 멈추면 메시지를 그대로 전하고, 메시지가 시키는 일을 안내해요
- 스크립트를 우회해서 git·gh 명령을 직접 실행하지 않아요. 검사(버전 형식, 커밋 수, main 포함 여부)를 건너뛰게 돼요
