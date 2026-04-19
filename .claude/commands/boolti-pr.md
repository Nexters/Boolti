---
description: 불티 프로젝트 컨벤션에 맞춰 develop 브랜치로 PR 생성
argument-hint: [선택: 초안/draft, 스크린샷 URL, 추가 메모]
---

`boolti-create-pr` 스킬을 사용해 현재 브랜치의 변경 사항으로 Pull Request를 생성한다.

스킬 워크플로우(Pre-flight 검증 → 브랜치/티켓/타입 추출 → 변경 요약 → 제목·바디 생성 → 원격 동기화 → 리뷰어·레이블·담당자 결정 → PR 생성 → 후처리)를 반드시 그대로 따른다. Pre-flight 검증이 실패하면 PR을 만들지 않는다.

추가 요청 사항:

$ARGUMENTS
