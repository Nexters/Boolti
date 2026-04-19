---
name: boolti-figma-to-feature
description: 피그마 디자인을 불티 프로젝트 Compose 화면으로 1:1 구현한다. Figma URL이 포함된 요청 또는 "이 디자인 구현해줘", "피그마대로 만들어줘", "디자인 적용해줘" 같은 요청에 트리거. 디자인 컨텍스트 추출 → 불티 디자인 시스템 매핑 → boolti-feature-planner 워크플로우 위임 순서로 진행.
---

# 불티 피그마 → 기능 구현

피그마 디자인을 추출해 불티 디자인 시스템에 매핑한 뒤, `boolti-feature-planner` 워크플로우를 그대로 따라 컨벤션을 지키며 Compose로 구현한다.

## 불티 피그마 구조 전제

화면은 Figma **SECTION** 노드로 계층화되어 있다. SECTION 이름이 그룹 라벨 역할.

일반 구조:
```
SECTION (대분류) > SECTION (중분류) > SECTION (세부 영역) | FRAME (화면)
```
예) `공연장 프로필 > 홈 > (01 빈 상태, 01 데이터 있음, 스크롤 시, SECTION "소개", SECTION "사진", …)`

화면 FRAME 판정 등 구체 필터링 규칙은 Step 2 (섹션 탐색 절차)에서만 다룬다.

## 워크플로우

### Step 0: Figma 토큰 준비 확인 (필수)

이 스킬의 모든 피그마 접근은 **Figma REST API + 개인 액세스 토큰**만 사용한다.

**토큰 존재 확인 (값은 절대 출력 금지):**
```bash
grep -c '^FIGMA_TOKEN=' local.properties
```
결과가 `1` 이면 준비 완료. `0` 이면 아래 순서로 발급 안내:

1. Figma → Settings → Security → Personal access tokens → **Generate new token**
2. 필수 스코프 체크:
   - ✅ **File content** → Read-only
   - ✅ **File metadata** → Read-only
   - ✅ Dev resources → Read-only (Code Connect 사용 시에만)
3. 생성 토큰을 `local.properties`에 추가 (`local.properties`는 이미 `.gitignore`에 포함):
   ```
   FIGMA_TOKEN="<pasted_token>"
   ```
4. 토큰은 한 번만 표시되므로 바로 붙여넣기. 교체/폐기는 같은 Settings 페이지.

**표준 호출 preamble** — Bash 도구 호출은 매번 새 셸이라 환경변수가 유지되지 않으니 아래 세 줄을 넣어야 한다. 실제로는 **같은 phase의 여러 curl을 하나의 Bash 호출에 `&&`로 묶어 preamble을 1회만 실행**하는 것이 원칙:
```bash
FIGMA_TOKEN=$(grep '^FIGMA_TOKEN=' local.properties | cut -d= -f2- | sed 's/^"//; s/"$//')
SKILL_CACHE=".claude/skills/boolti-figma-to-feature/.tmp/figma"
mkdir -p "$SKILL_CACHE"
```
- 캐시 디렉토리 `$SKILL_CACHE`는 스킬 폴더 내부이며 `.gitignore`로 제외돼 커밋되지 않는다.
- **재시도/재개 시 캐시 재사용**: `[ -s "$SKILL_CACHE/<file>" ] || curl ...` 식으로 이미 받은 응답이 있으면 재호출 생략.

**Figma REST 엔드포인트 구분 (혼동 주의):**
| 엔드포인트 | 용도 |
|-----------|------|
| `GET /v1/files/<fileKey>/nodes?ids=...&depth=N` | 노드 트리 JSON (레이아웃/토큰/텍스트) |
| `GET /v1/images/<fileKey>?ids=...&format=png` | **렌더** 스크린샷 PNG URL (S3 서명 URL 반환) |
| `GET /v1/files/<fileKey>/images` | `fills[].imageRef`에 해당하는 **원본 이미지 자산** URL 조회 |

**보안 규칙 (엄격):**
- 토큰 값을 `echo`/`print`/대화창/로그에 노출 금지. 응답 JSON만 파싱해서 요약.
- 캐시는 `$SKILL_CACHE` 경로로만 저장. 프로젝트 루트에 `.tmp/` 생성 금지.
- 403 `Invalid scope` 응답 → 토큰 스코프 부족. Step 0의 스코프 체크리스트로 재발급 유도.

### Step 1: Figma URL 파싱

```
figma.com/design/<fileKey>/<name>?node-id=<nodeId>
```
- `nodeId`의 `-`는 `:`로 변환 (예: `123-456` → `123:456`)
- 브랜치 URL: `figma.com/design/<fileKey>/branch/<branchKey>/...` → `branchKey`를 fileKey로 사용

### Step 2: 입력 모드 판단 및 대상 노드 결정

입력 노드의 타입을 먼저 확인한다 (`/v1/files/<fileKey>/nodes?ids=<nodeId>&depth=0` 또는 이미 받은 캐시의 `type` 필드).

| 모드 | 조건 | 처리 |
|------|------|------|
| **A: 단일 화면** | URL 1개, 입력 노드 `type=FRAME` + 모바일 viewport 폭(대략 320~450) | nodeId 하나를 대상으로 확정. Step 3으로. |
| **B: 명시적 다중 화면** | URL 2개 이상이 전부 화면 FRAME | 각 nodeId 수집. Step 3에서 순회. |
| **C: 섹션 탐색** | URL이 `type=SECTION`을 가리킴<br>또는 "이 영역 전체/그룹/화면 흐름" 같은 언급 | 아래 절차. 다중 URL이어도 각 SECTION 을 이 절차로 확장한 뒤 Step 3에서 합친다. |

**애매한 케이스 처리:**
- FRAME인데 모바일 viewport 폭이 아니면(배너/에셋/팝오버 단편) 자동 진행하지 말고 `AskUserQuestion`으로 의도 확인.
- 판단이 애매하면 `AskUserQuestion`으로 사용자에게 직접 모드 확인.

#### 섹션 탐색 절차 (모드 C)

1. 입력 SECTION의 하위 트리 조회 (`depth=3`이면 일반적으로 화면 FRAME까지 포함됨):
   ```bash
   curl -sS -H "X-Figma-Token: $FIGMA_TOKEN" \
     "https://api.figma.com/v1/files/<fileKey>/nodes?ids=<section_id>&depth=3" \
     -o "$SKILL_CACHE/section.json"
   ```
   세부 SECTION 밑에 추가 계층이 있어 화면 FRAME이 누락되는 경우에만 `depth=5`로 재요청.

2. 자식 재귀 순회 + 화면 FRAME 필터. 아래 skeleton을 사용:
   ```python
   import json
   d = json.load(open("<cache>/section.json"))
   root = d["nodes"]["<section_id>"]["document"]

   def is_screen(node):
       if node.get("type") != "FRAME":
           return False
       b = node.get("absoluteBoundingBox") or {}
       w, h = b.get("width", 0), b.get("height", 0)
       # 모바일 viewport 폭 (불티 canonical 375px 기준 ±20%). 높이는 긴 스크롤 화면 허용.
       if not (320 <= w <= 450 and h >= 400):
           return False
       # 폭 1000+ height<=200 형태의 레거시 banner, name='-' 에셋 단편은 제외
       return node.get("name", "").strip() not in ("", "-")

   def walk(node, path, screens):
       t, name = node.get("type"), node.get("name", "")
       here = path + [(t, name, node["id"])]
       if t == "SECTION":
           for c in node.get("children", []): walk(c, here, screens)
       elif is_screen(node):
           screens.append(here)
   screens = []; walk(root, [], screens)
   ```
   `COMPONENT`/`TEXT`/`INSTANCE` 등은 화면 목록에서 자동 제외됨.

3. 결과를 트리 형태로 사용자에게 제시하고 `AskUserQuestion`으로 확인:
   ```
   [SECTION: 공연장 프로필]
   ├─ [SECTION: 홈]
   │   ├─ 01 빈 상태
   │   ├─ 01 데이터 있음
   │   ├─ 스크롤 시
   │   └─ [SECTION: 소개]
   │       └─ …
   ├─ [SECTION: 대관 정보]
   │   └─ …
   └─ [SECTION: 문의처]
       └─ …

   n개 화면을 모두 포함할까요? 빠진/제외할 화면을 알려주세요.
   ```

4. 화면 수가 5개 초과면 단계별 분할 제안:
   > "화면이 많아 컨텍스트가 큽니다. 중분류 SECTION 단위로 나눠 순차 진행할까요?"

5. **Fallback**: 자식이 없거나 구조가 예상과 다르면 스크린샷으로 시각 재확인. 스크린샷 다운로드는 Step 3 (2)와 동일한 iteration 패턴을 사용 (단일 SECTION id만 주면 loop가 1회 돌고 끝). 그래도 해결이 안 되면 사용자에게 URL 직접 나열 요청 (모드 B로 전환).

### Step 3: 각 대상 노드에 대해 디자인 컨텍스트 추출

Step 2에서 확정된 모든 nodeId를 순회하며 두 가지를 수집한다. **두 호출은 독립적이므로 하나의 Bash 블록 또는 병렬 Bash 호출로 동시에 실행.**

**(1) 노드 트리 JSON** (레이아웃/토큰/텍스트/이미지 참조)
```bash
curl -sS -H "X-Figma-Token: $FIGMA_TOKEN" \
  "https://api.figma.com/v1/files/<fileKey>/nodes?ids=<id1>,<id2>,...&depth=5" \
  -o "$SKILL_CACHE/<feature>_nodes.json"
```
- 기본 `depth=5`로 시작 (대부분의 Compose 화면은 6~8 레벨 중 의미 있는 레이아웃이 5 내외).
- 파싱해 보니 내부 구조가 더 필요하면 `depth=10`으로 escalate 후 재요청. `depth=20`은 거의 쓰지 말 것 (응답 크기 폭증).

JSON에서 읽을 주요 필드:
- 레이아웃: `layoutMode` (HORIZONTAL/VERTICAL), `primaryAxisAlignItems`, `counterAxisAlignItems`, `itemSpacing`, `paddingLeft/Right/Top/Bottom`
- 크기: `absoluteBoundingBox`, `size`, `constraints`
- 외곽: `cornerRadius`, `rectangleCornerRadii`, `strokes`, `strokeWeight`, `effects`
- 색상: `fills[].color` (r,g,b,a 0~1 float → hex 변환), `backgroundColor`, 변수 참조는 `fills[].boundVariables`
- 텍스트: `characters`, `style.fontFamily/fontSize/fontWeight/lineHeightPx/letterSpacing/textAlignHorizontal`
- 이미지 자산: `fills[].imageRef` → 원본 URL은 `GET /v1/files/<fileKey>/images` 응답의 `meta.images[<imageRef>]`에서 조회 (위 엔드포인트 표 참고)
- 컴포넌트 인스턴스: `componentId`, `componentProperties`

**(2) 스크린샷 PNG** (시각 검증용, `scale=2` 고해상도, **병렬 다운로드**)
```bash
mkdir -p "$SKILL_CACHE/screenshots" && \
curl -sS -H "X-Figma-Token: $FIGMA_TOKEN" \
  "https://api.figma.com/v1/images/<fileKey>?ids=<id1>,<id2>,...&format=png&scale=2" \
  -o "$SKILL_CACHE/<feature>_images.json" && \
python3 -c "
import json
d=json.load(open('$SKILL_CACHE/<feature>_images.json'))
for nid, url in d['images'].items():
    if url: print(nid.replace(':','_'), url)
" | xargs -n 2 -P 4 sh -c 'curl -sS "$1" -o "$SKILL_CACHE/screenshots/$0.png"'
# 이후 Read tool로 개별 PNG 로드
```

각 화면별로 파악:
- 레이아웃 구조, 디자인 토큰, 자산(텍스트/아이콘/이미지), 인터랙션, 디자인 어노테이션(노드 이름에 담긴 상태 표시, 예: `01 빈 상태`, `스크롤 시`)

**다중 화면일 때 추가 정리 (중요):**
개별 화면 데이터만 나열하지 말고 **화면 간 관계**를 반드시 요약한다. 계획 품질이 여기서 갈린다.
- **공유 컴포넌트**: 여러 화면에서 반복 등장하는 UI (상단바, 리스트 아이템 등) → 재사용 컴포넌트화
- **Navigation 흐름**: A → B로 이동하는 트리거와 전달 데이터
- **공유 상태/데이터**: 같은 엔티티를 여러 화면에서 표시/수정하는 경우
- **Variant 관계**: 한 플로우의 상태 변형 (빈 상태 / 로딩 / 에러 / 정상) — 별도 화면이 아니라 같은 화면의 UiState로 통합 가능한지 판단

### Step 4: 불티 디자인 시스템 매핑

`presentation/theme/`의 기존 토큰과 매핑한다.

| Figma 추출값 | 매핑 대상 | 위치 |
|--------------|-----------|------|
| 색상 (hex) | `Theme.colors.*` | `theme/Color.kt` |
| 폰트 (family/size/weight) | `Theme.typography.*` | `theme/Type.kt` |
| 간격 (dp) | dimens 또는 inline dp | `theme/Dimen.kt` |
| 컴포넌트 | 기존 재사용 컴포넌트 우선 | `presentation/component/` |

매칭 안 되는 토큰 발견 시:
1. 기존 토큰을 잘못 매핑한 건 아닌지 재확인
2. 그래도 신규 토큰 필요하면 → 사용자 승인 후 디자인 시스템에 추가

### Step 5: boolti-feature-planner 워크플로우 위임

추출한 디자인 컨텍스트와 매핑 결과를 가지고 `boolti-feature-planner` 스킬의 워크플로우를 **그대로** 따른다 (계획 작성 → 사용자 승인 → Phase별 실행 → Quality Gate).

계획 문서(`docs/plans/PLAN_<feature>.md`)에 다음을 추가한다:
- Figma URL 전체 (단일/다중 모두 References 섹션에 나열)
- 디자인 토큰 매핑 결과 (Architecture Decisions 섹션)
- 신규 디자인 토큰 추가 여부 (있으면 별도 Phase로 분리)
- **다중 화면일 때**: Step 3에서 정리한 화면 간 관계(공유 컴포넌트 / Navigation / 공유 상태)를 Architecture Decisions에 명시하고, Phase를 화면 단위 대신 "공통 컴포넌트 → 화면 1 → 화면 2 → navigation 연결" 식으로 분할 검토

### Step 6: 시각 정합성 검증

각 Phase의 Quality Gate에 다음을 추가:
- [ ] Figma 스크린샷과 시각적으로 1:1 일치 (디바이스/Preview에서 확인)
- [ ] 임의의 hex/dp 값 사용 없음 (모두 디자인 토큰 경유)
- [ ] `verify-compose-conventions` 스킬 통과

최종 비교 시 Step 3 (2)와 동일한 방법으로 `scale=2` 스크린샷을 다시 받아 구현 결과와 나란히 검토한다.

## 원칙

- **Compose만** — JSON 응답과 스크린샷을 참고해 Compose + 불티 디자인 시스템으로 직접 구현한다.
- **재사용 우선** — `presentation/component/`에 비슷한 컴포넌트가 있으면 무조건 먼저 활용. 신규 컴포넌트 만들기 전에 검색.
- **계획 우선** — boolti-feature-planner 위임 단계에서 계획 작성과 승인이 필수. 디자인을 받았다고 바로 구현 시작 금지.
- **토큰 보안** — `FIGMA_TOKEN` 값을 대화/로그/커밋에 노출 금지. 스킬 폴더 내 `.tmp/` 캐시도 커밋 금지.
