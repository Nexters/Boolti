---
name: boolti-feature-planner
description: 불티 프로젝트의 기능 개발(새 화면, ViewModel/Repository/UseCase, API 연동, 디자인 적용 등 신규 작업)을 phase 기반 계획에 따라 컨벤션을 지키며 진행한다. 단순 버그 수정·문구 변경·소규모 리팩토링에는 사용하지 않는다. 키워드: 기능 추가, 화면 추가, feature 구현, plan, planning.
---

# 불티 기능 개발 플래너

불티 프로젝트의 기능을 phase 기반 계획에 따라 컨벤션을 지키며 구현한다.

## ⚠️ 핵심 원칙

**모든 구현 작업은 반드시 계획부터 시작한다.** 간단한 구현이라도 예외 없음.

- 계획 없이 바로 코드 수정 금지
- 사용자 승인 전까지 구현 시작 금지
- 각 phase 완료 후 Quality Gate 통과 전까지 다음 phase 진행 금지

## 워크플로우

### Step 1: 컨벤션 로드

`CLAUDE.md`(프로젝트 루트)를 단일 진실 공급원(single source of truth)으로 삼는다. 모듈 구조, 코드 위치 가이드, 패턴, API 규칙은 모두 거기에 있다. 추가로 작업 영역과 유사한 기존 구현 1~2개를 읽어 코드 스타일을 파악한다.

### Step 2: 요구사항 분석

1. 관련 파일 읽고 기존 구조와 패턴 파악
2. 영향받는 모듈, 의존성, 통합 지점 식별
3. 복잡도 판단 (Small / Medium / Large)
4. **API 작업이면 https://dev.api.boolti.in/v3/api-docs/app 스펙을 먼저 확인**

### Step 3: Phase 분할

3~7개 phase로 분할한다. 각 phase 조건:
- 1~4시간 분량
- 단독 빌드/실행 가능한 결과물 산출
- 명확한 성공 기준
- 개별 롤백 가능

각 phase는 다음을 포함한다.
- Goal (이 phase가 만들어 내는 동작 가능한 결과)
- Tasks (선택적 TDD: 필요한 경우만 RED → GREEN → REFACTOR)
- Quality Gate 체크리스트
- 의존성, 롤백 방법

### Step 4: 계획 문서 작성

`plan-template.md` 형식을 따라 `docs/plans/PLAN_<feature-name>.md`를 생성한다.
- `docs/plans/` 디렉토리가 없으면 생성
- 파일명은 kebab-case (예: `PLAN_show-detail-screen.md`)

### Step 5: 사용자 승인 (필수)

`AskUserQuestion`으로 명시적 승인을 받는다.
- "Phase 분할이 적절한가?"
- "우려되는 부분이 있는가?"
- "이대로 진행해도 좋은가?"

승인 받기 전까지 절대 구현 시작 금지.

### Step 6: Phase 별 실행

승인 후 phase 순서대로 실행한다.
1. 해당 phase 작업 수행
2. 완료한 task의 checkbox 표시
3. **Quality Gate 실행**: `bash .claude/skills/boolti-feature-planner/scripts/quality-gate.sh`
4. 실패 시 멈추고 수정 후 재실행
5. 모두 통과해야 다음 phase로 진행

## 플래너 전용 규칙

CLAUDE.md에 없는, 이 스킬에서만 강제하는 규칙.

**의존 방향 (위반 절대 금지)**
- presentation → domain (data 직접 참조 금지)
- data → domain
- domain은 Android/외부 의존 없음 (pure Kotlin)

**코드 품질 차단 항목** (Quality Gate에서 자동 검출)
- 와일드카드 import
- 프로젝트 클래스(`com.nexters.boolti.*`)를 fully-qualified name으로 직접 사용

**Compose 작성/수정 시**
- `verify-compose-conventions` 스킬 실행 (사용자 글로벌 스킬, `~/.claude/skills/`)

## Quality Gate

각 phase 완료 후 다음 스크립트를 실행해 모두 통과해야 한다.

```bash
bash .claude/skills/boolti-feature-planner/scripts/quality-gate.sh
```

스크립트가 실행하는 검증:

| 항목 | 명령 | 차단 여부 |
|------|------|-----------|
| 빌드 | `./gradlew assembleDebug` | 차단 |
| 단위 테스트 | `./gradlew btTest` | 차단 (테스트 추가/수정한 경우) |
| Ktlint | `./gradlew ktlintCheck` | 차단 (도입 후) |
| 와일드카드 import | `scripts/check-imports.sh` | 차단 |
| Full path 사용 | `scripts/check-fullpath.sh` | 경고 |

### Ktlint 미설치 시 처리

`./gradlew ktlintCheck` 실행 시 task가 없다는 에러가 발생하면:

1. 사용자에게 알린다: "Ktlint가 설정되어 있지 않습니다. 도입할까요? (baseline 적용으로 기존 코드는 무시, 신규 코드만 검사)"
2. **승인 시** 다음 작업 수행
   - 루트 `build.gradle.kts`에 `org.jlleitschuh.gradle.ktlint` plugin 추가
   - 각 모듈에 plugin 적용
   - `./gradlew ktlintCheck` 1회 실행해 violation 확인
   - `./gradlew ktlintGenerateBaseline` 으로 baseline 생성 (기존 violation을 baseline에 등록)
   - 다시 `ktlintCheck` 실행해 통과 확인
3. **거부 시** ktlint 단계는 skip하고 진행

다른 도구(Spotless, detekt 등)도 동일한 절차: 사용자 승인 후 설치, baseline 가능하면 사용.

## 선택적 TDD

Boolti는 strict TDD를 강제하지 않는다. 다음 기준으로 판단한다.

**테스트 작성 권장**
- Domain UseCase (pure Kotlin, 비즈니스 로직)
- Repository 구현체의 데이터 변환 로직
- 복잡한 ViewModel 상태 전이 (조건 분기 多)
- 회귀 위험이 큰 영역

**테스트 작성 면제**
- 단순 데이터 클래스 (data class만 정의)
- Compose UI (Preview로 대체)
- 단순 위임 코드 (UseCase가 Repository를 그대로 호출만)
- "당연한" 테스트 (getter가 값 반환하는지 같은)

**판단 기준**: "이 테스트가 미래에 회귀를 잡아낼 수 있는가?"가 의심되면 작성하지 않는다.

테스트 작성한 phase에서만 Quality Gate의 `btTest` 통과 필요. 작성 안 했으면 변경 영향이 있을 수 있는 모듈만 테스트 실행해도 됨.

## 참고 파일

- [plan-template.md](plan-template.md) — 계획 문서 템플릿
- `scripts/quality-gate.sh` — 전체 검증 실행
- `scripts/check-imports.sh` — 와일드카드 import 검출
- `scripts/check-fullpath.sh` — Fully-qualified class name 사용 검출
