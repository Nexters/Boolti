---
name: boolti-hotfix-cancel
description: 실수로 시작한 불티 핫픽스를 취소한다. hotfix/* 브랜치에 버전 커밋 하나만 있을 때만 PR을 닫고 브랜치와 빈 마일스톤을 지워 시작 전 상태로 돌린다. "핫픽스 취소", "핫픽스 잘못 땄어", "hotfix 브랜치 없애줘" 같은 요청에 트리거. 릴리즈 취소는 boolti-release-cancel을 쓴다.
---

# 핫픽스 취소

`.github/scripts/release/cancel.sh hotfix`를 실행하는 스킬이에요. 판단·실행 로직은 전부 스크립트에 있고, 스킬은 미리보기 → 승인 → 실행만 해요. 흐름 전체는 `.github/scripts/release/README.md`를 봐요.

## 순서

1. 미리보기를 실행하고 출력 그대로 보여줘요
   ```bash
   .github/scripts/release/cancel.sh hotfix
   ```
   - 사용자가 버전을 말했으면 `hotfix` 뒤에 붙여요 (예: `cancel.sh hotfix 1.15.0`)
2. 사용자 승인을 받아요. 원격 브랜치·PR·태그를 바꾸는 작업이라 승인 없이 넘어가지 않아요
3. 같은 명령에 `--apply`를 붙여 실행해요
4. 결과(삭제한 브랜치·마일스톤)를 알려줘요

## 실패했을 때

- 스크립트가 `❌`로 멈추면 메시지를 그대로 전하고, 메시지가 시키는 일을 안내해요
- 스크립트를 우회해서 git·gh 명령을 직접 실행하지 않아요. 검사(버전 형식, 커밋 수, main 포함 여부)를 건너뛰게 돼요
