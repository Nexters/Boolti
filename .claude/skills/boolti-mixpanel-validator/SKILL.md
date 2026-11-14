---
name: boolti-mixpanel-validator
description: 불티 AppTracker(Mixpanel) 이벤트 구현을 공식 컨벤션에 맞게 검증·제안한다. `AppTracker.view/click/impression/search/complete`, `Screen.X`, `Role.X` 등 트래킹 코드를 추가·수정·리뷰할 때, 또는 "트래킹 추가해줘", "이벤트 로깅", "Mixpanel 이벤트 검증", "AppTracker 리뷰" 같은 요청에 트리거.
---

# 불티 Mixpanel 이벤트 검증 스킬

`common/tracker` 모듈(`AppTracker`) 기반 이벤트 트래킹을 **타입 안전 API + 공식 이벤트 규칙**에 맞게 검증한다.

## ⚠️ 원칙

- **타입 안전 확장 함수만 쓴다.** `trackEvent("Click", ...)` 같은 문자열 호출은 커스텀 이벤트 외에는 금지.
- **`Screen`/`Role`은 코드가 진실.** 사용 가능한 값은 `common/tracker/src/main/java/com/nexters/boolti/common/tracker/field/{Screen,Role}.kt`에서 확인 (스킬에 복붙하지 않는다 — 드리프트 위험).

## AppTracker API

| 함수 | 기록 이벤트명 | 자동 주입 필드 |
|------|--------------|----------------|
| `view(screen, properties)` | `View Screen` | `screen` |
| `click(screen, objectRole, objectValue, properties)` | `Click` | `screen`, `object_role`, `object_value` |
| `clickLink(screen, objectValue, url, properties)` | `Click` | `screen`, `object_role=Link`, `object_value`, `url` |
| `impression(screen, objectRole, objectValue, properties)` | `Impression` | `screen`, `object_role`, `object_value` |
| `search(screen, keyword, properties)` | `Search` | `screen`, `search_keyword` |
| `complete(target, properties, trimPrefix=true)` | `Complete <target>` | — |
| `identify(userId)` / `identify(properties, userId)` | people set | — |
| `trackEvent(eventName, properties)` | (그대로) | — |
| `withTrackTime(eventName) { ... }` | (그대로) | `duration` |

헬퍼를 쓰면 **필수 필드는 자동 주입**된다. 문자열 API(`trackEvent`)를 직접 쓸 때만 필수 필드 누락을 수동 검사.

상세 사용법은 `common/tracker/README.md` 참조.

## 공식 이벤트 규칙

**네이밍**
- 필드명: 소문자 snake_case
- 필드값 (카테고리성: screen·role·타입 토큰 등): 공백 없는 PascalCase (`ConcertDetail`, `Kakao`)
- 필드값 (UI 라벨·사용자 자유 입력): 원문 허용 (`"결제하기"`, `search_keyword`의 검색어)
  - `object_value`가 식별자/카테고리 토큰이면 PascalCase, 사용자에게 보이는 UI 라벨이면 원문
- Boolean: `"Y"` / `"N"` 문자열 (`true`/`false` 금지)
- 금액: 콤마 없는 숫자 (`15000`, `"15,000"` ❌)

**이벤트별 필수 필드** — 헬퍼 사용 시 자동 주입됨

| 이벤트 | 필수 필드 |
|--------|-----------|
| `View Screen` | `screen` |
| `Click` | `screen`, `object_role`, `object_value` (Role=Link 시 `url` 추가) |
| `Impression` | `screen`, `object_role`, `object_value` |
| `Search` | `screen`, `search_keyword` |
| `Complete XXX` | 없음 (context 필드 권장) |

**`object_role`** — Role.kt에 정의된 값만 사용. 새 값 추가는 팀 협의 후.

**주요 property**
- `rank`: 목록 내 순서. 1부터 시작하는 `Int`.

## 검사 관점 (체크리스트)

1. **헬퍼 선택**
   - `trackEvent("Click", ...)` → `click(...)`
   - `click(..., Role.Link, ..., properties=mapOf("url" to ...))` → `clickLink(...)`
   - `trackEvent("Complete X")` → `complete("X")`
2. **Screen/Role 확장 프로퍼티 사용**
   - `Screen("Home")`·`Role("Button")` 직접 생성 금지 → `Screen.Home`·`Role.Button`
   - 없는 Screen이면 먼저 `Screen.kt`에 확장 프로퍼티 추가: `val Screen.Companion.X get() = Screen("X")`
3. **properties 값 타입**
   - 카테고리 값은 PascalCase, UI 라벨/자유 입력은 원문
   - Boolean → `"Y"`/`"N"`, 금액 → 숫자 (콤마 없음), `rank` → `Int` (1부터)
4. **자동 주입 필드 덮어쓰기 금지**
   - 헬퍼가 이미 넣는 `screen`, `object_role`, `object_value`, `search_keyword`를 `properties`에 또 넣으면 덮어써짐 → 제거
5. **민감정보 금지**
   - 비밀번호, 주민번호, 카드번호, 토큰 등

**관행 (강제 아님)**: Composable 진입은 `LaunchedEffect(Unit) { AppTracker.view(Screen.X) }`, 클릭은 ViewModel 또는 UI 콜백에서. `withTrackTime` 블록 내 예외 시 이벤트 누락 가능.

## 출력 포맷

문제 없으면 "✅ 규칙 위반 없음" 한 줄. 문제가 있을 때만 아래 구조로.

```markdown
## ❌ 수정 필요
1. **파일:라인** — 위반 요약
   - 문제: <규칙 위반 내용>
   - 수정안:
     ```kotlin
     // Before
     AppTracker.trackEvent("Click", mapOf("screen" to "home", ...))
     // After
     AppTracker.click(screen = Screen.Home, objectRole = Role.Button, objectValue = "예매")
     ```

## 💡 제안 (선택)
- 추가하면 좋을 context property, Screen 확장 프로퍼티 추가 등
```

## 대표 예시

### 목록 항목 impression + rank
```kotlin
shows.forEachIndexed { index, show ->
    AppTracker.impression(
        screen = Screen.Home,
        objectRole = Role.Item,
        objectValue = "ShowCard",
        properties = mapOf(
            "show_id" to show.id,
            "rank" to index + 1,
        ),
    )
}
```

### Complete 이벤트 (규칙 전체 적용)
```kotlin
AppTracker.complete(
    target = "Payment",                  // → "Complete Payment"
    properties = mapOf(
        "payment_method" to "Card",       // 카테고리 값: PascalCase
        "amount" to 15_000,                // 금액: 콤마 없는 숫자
        "is_first_purchase" to "Y",        // Boolean: Y/N
    ),
)
```

## 참고

- 구현: `common/tracker/src/main/java/com/nexters/boolti/common/tracker/`
- 사용 가이드: `common/tracker/README.md`
- Mixpanel 토큰: `DEV_MIXPANEL_TOKEN` / `PROD_MIXPANEL_TOKEN` (`local.properties`)
