# Implementation Plan: [Feature Name]

**Status**: 🔄 In Progress
**Started**: YYYY-MM-DD
**Last Updated**: YYYY-MM-DD
**Estimated Completion**: YYYY-MM-DD

---

**⚠️ CRITICAL INSTRUCTIONS**: 각 phase 완료 후

1. ✅ 완료한 task의 checkbox 표시
2. 🧪 Quality Gate 스크립트 실행: `bash .claude/skills/boolti-feature-planner/scripts/quality-gate.sh`
3. ⚠️ Quality Gate 항목 모두 통과 확인
4. 📅 "Last Updated" 날짜 갱신
5. 📝 Notes 섹션에 학습 내용 기록
6. ➡️ 그 후에만 다음 phase로 진행

⛔ **Quality Gate 실패 상태로 다음 phase 진행 금지**

---

## 📋 Overview

### Feature Description
[이 기능이 무엇을 하고 왜 필요한지]

### Success Criteria
- [ ] 기준 1
- [ ] 기준 2
- [ ] 기준 3

### User Impact
[사용자/제품에 어떤 가치를 주는지]

---

## 🏗️ Architecture Decisions

| Decision | Rationale | Trade-offs |
|----------|-----------|------------|
| [결정 1] | [선택한 이유] | [포기한 것] |
| [결정 2] | [선택한 이유] | [포기한 것] |

### 영향받는 모듈

- [ ] `domain` — [어떤 변경]
- [ ] `data` — [어떤 변경]
- [ ] `presentation` — [어떤 변경]
- [ ] `tosspayments` — [어떤 변경, 해당 시]
- [ ] `app` — [어떤 변경, 해당 시]

### 의존 방향 검증
- [ ] presentation → domain (data 직접 참조 없음)
- [ ] data → domain
- [ ] domain은 외부 의존 없음

---

## 📦 Dependencies

### 시작 전 확보 필요
- [ ] [의존 1: API 스펙, 디자인 등]
- [ ] [의존 2]

### API (해당 시)
- Swagger 확인: https://dev.api.boolti.in/v3/api-docs/app
- Endpoint: `[METHOD] /path`
- 인증 필요: Yes / No

### 외부 라이브러리 (신규 추가 시)
- [ ] [라이브러리 X: version, 추가 위치 모듈]

---

## 🧪 Test Strategy

**선택적 TDD 원칙**: 회귀 위험이 있고 복잡한 로직만 테스트 작성. 자명한 코드는 테스트 면제.

### 테스트 작성 대상
- [ ] [UseCase A — 분기 로직 多]
- [ ] [Repository B — 데이터 변환 로직]
- [ ] [ViewModel C — 상태 전이 복잡]

### 테스트 면제 (사유 명시)
- [Composable X — Preview로 검증]
- [data class Y — 단순 모델]

---

## 🚀 Implementation Phases

### Phase 1: [Phase Name]
**Goal**: [이 phase가 만들어 내는 동작 가능한 결과]
**Estimated Time**: X hours
**Status**: ⏳ Pending

#### Tasks

> 테스트 작성하는 경우 RED → GREEN → REFACTOR 순서. 아니면 구현 task만.

**🔴 RED (테스트 작성 시)**
- [ ] [Test 1.1] `[테스트 파일 경로]` — [어떤 케이스]

**🟢 GREEN / 구현**
- [ ] [Task 1.2] `[구현 파일 경로]` — [무엇을 구현]
- [ ] [Task 1.3] `[구현 파일 경로]` — [무엇을 구현]

**🔵 REFACTOR (필요 시)**
- [ ] [Task 1.4] [리팩토링 대상과 목표]

#### Quality Gate ✋

**다음 모두 통과 전까지 Phase 2 진행 금지**

```bash
bash .claude/skills/boolti-feature-planner/scripts/quality-gate.sh
```

자동 검증 (스크립트가 실행)
- [ ] 빌드 성공: `./gradlew assembleDebug`
- [ ] Ktlint 통과 (도입된 경우)
- [ ] 와일드카드 import 없음
- [ ] Full path 사용 없음 (경고)

수동 검증 (테스트 작성한 경우)
- [ ] 단위 테스트 통과: `./gradlew btTest`

수동 검증 (Compose 추가/수정한 경우)
- [ ] `verify-compose-conventions` 스킬 통과

수동 검증 (기능)
- [ ] [기능 1 동작 확인]
- [ ] [엣지 케이스 X 처리 확인]

#### Rollback
- [ ] [되돌릴 파일/변경 사항]

---

### Phase 2: [Phase Name]
**Goal**: [동작 가능한 결과]
**Estimated Time**: X hours
**Status**: ⏳ Pending

#### Tasks

**🔴 RED (테스트 작성 시)**
- [ ] [Test 2.1]

**🟢 GREEN / 구현**
- [ ] [Task 2.2]
- [ ] [Task 2.3]

**🔵 REFACTOR (필요 시)**
- [ ] [Task 2.4]

#### Quality Gate ✋
[Phase 1과 동일]

#### Rollback
- [ ] [되돌릴 파일/변경 사항]

---

### Phase 3: [Phase Name]
[동일한 구조로 반복]

---

## ⚠️ Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| [API 스펙 변경] | Low/Med/High | Low/Med/High | [구체적 대응] |
| [성능 저하] | Low/Med/High | Low/Med/High | [구체적 대응] |
| [기존 기능 회귀] | Low/Med/High | Low/Med/High | [구체적 대응] |

---

## 📝 Notes & Learnings

### 구현 중 발견
- [예상과 달랐던 부분, 변경한 결정 등]

### Blocker
- [Blocker 설명] → [해결 방법]

### 다음에 개선할 점
- [회고]

---

## 📚 References

- 디자인: [Figma URL]
- API 스펙: https://dev.api.boolti.in/v3/api-docs/app
- 관련 이슈: Boolti-XXX
- 관련 PR: #XXX

