# Tracker Module

Boolti 앱의 사용자 행동 분석을 위한 트래킹 유틸리티 모듈입니다. Mixpanel을 기반으로 구현되었으며, 타입 안전한 이벤트 트래킹을 제공합니다.

## 목차

- [설정](#설정)
- [초기화](#초기화)
- [이벤트 트래킹](#이벤트-트래킹)
- [사용자 식별](#사용자-식별)
- [고급 기능](#고급-기능)
- [예시 코드](#예시-코드)
- [주의사항](#주의사항)

## 설정

### 1. Mixpanel Token 설정

`local.properties` 파일에 Mixpanel 토큰을 추가합니다:

```properties
# Development 환경 토큰
DEV_MIXPANEL_TOKEN="your_dev_mixpanel_token"

# Production 환경 토큰
PROD_MIXPANEL_TOKEN="your_prod_mixpanel_token"
```

### 2. 의존성

모듈의 `build.gradle.kts`에는 이미 필요한 의존성이 포함되어 있습니다:

- Mixpanel Android SDK
- Timber (로깅)
- Kotlinx Serialization (로그 포맷팅)

## 초기화

Application 클래스의 `onCreate()`에서 `AppTracker`를 초기화합니다.

### 기본 초기화

```kotlin
class BooltiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Timber는 AppTracker보다 먼저 초기화되어야 합니다
        Timber.plant(Timber.DebugTree())

        // 기본 초기화 (flushBatchSize = 50)
        AppTracker.initialize(this)
    }
}
```

### 고급 초기화

```kotlin
AppTracker.initialize(
    context = this,
    flushBatchSize = 100, // 한번에 전송할 이벤트 개수 (기본값: 50)
    superProperties = mapOf(
        "app_version" to BuildConfig.VERSION_NAME,
        "device_type" to "android"
    ) // 모든 이벤트에 자동으로 추가될 속성
)
```

## 이벤트 트래킹

### 1. View Screen - 화면 진입 트래킹

사용자가 특정 화면에 진입했을 때 사용합니다.

```kotlin
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.view
import com.nexters.boolti.common.tracker.field.Screen

// 기본 사용
AppTracker.view(Screen.Home)

// 추가 속성과 함께
AppTracker.view(
    screen = Screen.ShowDetail,
    properties = mapOf(
        "show_id" to "123",
        "show_name" to "밴드 콘서트"
    )
)
```

### 2. Click - 클릭 이벤트 트래킹

사용자가 버튼, 링크 등을 클릭했을 때 사용합니다.

```kotlin
import com.nexters.boolti.common.tracker.event.click
import com.nexters.boolti.common.tracker.field.Role

AppTracker.click(
    screen = Screen.Payment,
    objectRole = Role.Button,
    objectValue = "결제하기"
)

// 추가 속성과 함께
AppTracker.click(
    screen = Screen.ShowDetail,
    objectRole = Role.Link,
    objectValue = "공유하기",
    properties = mapOf(
        "show_id" to "123",
        "share_type" to "kakao"
    )
)
```

**사용 가능한 Role 타입:**
- `Role.Button` - 버튼
- `Role.BottomSheet` - 바텀시트
- `Role.Popup` - 팝업
- `Role.Banner` - 배너
- `Role.Link` - 링크
- `Role.Tab` - 탭

### 3. Impression - 노출 이벤트 트래킹

특정 화면이나 요소가 사용자에게 노출되었을 때 사용합니다.

```kotlin
import com.nexters.boolti.common.tracker.event.impression

AppTracker.impression(
    screen = Screen.Home,
    objectRole = Role.Banner,
    objectValue = "PromotionBanner",
    properties = mapOf(
        "banner_id" to "promotion_001"
    )
)
```

#### Compose 컴포넌트 노출 감지

- `Modifier.impression`으로 지정 비율 이상 노출되었을 때 기록함.
- 기본은 50% 노출이며, 스크롤 아웃 후 돌아오면 다시 기록함.
- 노출 중 리컴포지션이나 위치 변경만으로 다시 기록하지 않음.
- **이벤트 전달은 한 경로만 사용함**: 로컬 `onImpressed`가 있으면 우선 호출하고, 없으면 `ImpressionState`의 중앙 콜백으로 전달함.

```kotlin
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.impression
import com.nexters.boolti.common.tracker.field.Banner
import com.nexters.boolti.common.tracker.field.Home
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.impression.impression

// key와 state 생략 가능. key는 랜덤 String으로 자동 생성함.
Box(
    modifier = Modifier.impression(
        threshold = 0.5f,
        extras = { mapOf("banner_id" to banner.id) },
    ) { extras ->
        AppTracker.impression(
            screen = Screen.Home,
            objectRole = Role.Banner,
            objectValue = "PromotionBanner",
            properties = extras,
        )
    },
)
```

#### 여러 컴포넌트의 로그를 한 곳에서 기록

```kotlin
import com.nexters.boolti.common.tracker.field.Item
import com.nexters.boolti.common.tracker.impression.rememberImpressionState

// rememberSaveable 기반. 기본값 deduplicate=false는 재노출마다 기록함.
val impressionState = rememberImpressionState { event ->
    AppTracker.impression(
        event = event,
        screen = Screen.Home,
        objectRole = Role.Item,
        objectValue = "ShowCard",
    )
}

LazyColumn {
    itemsIndexed(shows, key = { _, show -> show.id }) { index, show ->
        ShowCard(
            modifier = Modifier.impression(
                key = show.id,
                state = impressionState,
                extras = { mapOf("show_id" to show.id, "rank" to index + 1) },
            ),
        )
    }
}
```

- 중앙 콜백은 `ImpressionEvent(key: Any, threshold: Float, extras: Map<String, Any>)` 수신함.
- 이벤트의 threshold는 설정한 기준값임. 실제 측정 비율은 아님.
- 새 AppTracker 오버로드는 extras를 기존 properties로 전달함. key와 threshold는 자동 전송하지 않음.
- extras에 `screen`, `object_role`, `object_value`를 넣으면 새 오버로드가 오류로 거부함.
- Flow 대신 생성자 콜백을 제공함. 수집 시작 전 이벤트 유실이나 재수집 중복 없이 직접 수신함.

#### ViewModel에서 상태 소유

```kotlin
import com.nexters.boolti.common.tracker.impression.ImpressionState

class HomeViewModel : ViewModel() {
    val impressionState = ImpressionState(deduplicate = true) { event ->
        AppTracker.impression(
            event = event,
            screen = Screen.Home,
            objectRole = Role.Item,
            objectValue = "ShowCard",
        )
    }
}

// Composable에서 전달
Modifier.impression(key = show.id, state = viewModel.impressionState)
```

| 항목 | 동작 |
|---|---|
| `deduplicate = false` — 기본값 | 기준 미달 → 충족으로 바뀔 때마다 기록 |
| `deduplicate = true` | state에서 key별 1회만 기록 |
| 같은 state의 동일 key 여러 개 | 하나의 대상으로 취급. 모두 기준 미달이 된 뒤 다시 충족할 때 재노출 |
| key 생략 | 호출 위치별 UUID 문자열 생성. 리컴포지션·저장 복원 시 유지 |
| key 직접 지정 | 목록이나 ViewModel에서 동일 데이터를 지속해서 식별할 때 사용 |
| threshold | 0~1의 유한한 Float. 기준과 같은 비율도 포함. 기본 0.5f |
| threshold 0 | 크기가 있는 첫 실제 배치 시 기록. 화면 밖 배치도 포함. 단순 사전 구성으로는 기록하지 않음 |
| extras 람다 | 발생 확정 시 한 번 평가하고 Map 복사. 가벼운 값 구성만 수행 |
| `reset(key)` / `resetAll()` | 기록 이력 제거. 이미 노출 중이면 다음 노출 구간부터 재기록 |

- key는 `Any`를 받으며 `equals`/`hashCode` 기준으로 비교함. 예: `123`, `"123"`은 서로 다른 key임. 비교 결과가 변하지 않는 불변 값을 사용함.
- `deduplicate = true` 상태를 저장 복원할 때는 key도 Android에서 저장 가능한 타입이어야 함. Int·Long·String·Parcelable·Serializable 등이 해당함. 일반 객체 key는 ViewModel에서 생성한 상태처럼 저장하지 않는 상태에서 사용 가능함.

#### 저장 복원과 수명

- `rememberImpressionState`는 정책과 기록 이력을 저장 복원하고 최신 중앙 콜백을 다시 연결함.
- 직접 생성도 가능함: `rememberSaveable(saver = ImpressionState.Saver) { ImpressionState() }`.
- 직접 Saver 사용 시 콜백은 저장하지 않으므로 로컬 `onImpressed`를 전달함. 중앙 콜백 복원이 필요하면 헬퍼 사용을 권장함.
- ViewModel에서 생성하면 화면 회전에 유지됨. 프로세스 종료 후 복원은 기본 제공하지 않음.
- 기본 상태는 과거 key를 누적하지 않음. 중복 제한 상태는 key가 쌓이므로 화면 범위로 사용하고 큰 무한 목록의 저장 용량에 유의함.
- 자동 key의 저장 상태가 사라지면 새 key가 생성됨. 화면 재생성 뒤에도 ViewModel의 중복 이력을 적용하려면 명시적 데이터 key 사용.
- 콜백 실행 전에 중복 이력을 기록함. 콜백 오류 시 자동 재전송하지 않으며 서버 수신 성공까지 보장하지 않음.
- ImpressionState 변경과 콜백은 메인 스레드에서 실행함. ViewModel 콜백에서 Activity나 View를 보관하지 않음.

#### 면적 기준과 성능

- 부모 clip과 Compose 루트 경계가 반영된 창 좌표의 사각형 면적을 사용함. 큰 항목도 항목 전체 면적을 분모로 사용함.
- 다른 UI가 덮는 픽셀, 투명도, 둥근 모양의 정확한 픽셀 면적은 계산하지 않음.
- 호스트 lifecycle이 STARTED 이상이고 View가 표시된 상태에서 판정함. 비활성 상태에서 활성화되면 재판정함.
- `Modifier.impression().padding(...)`은 padding을 포함한 크기, `Modifier.padding(...).impression()`은 padding 안쪽 크기를 기준으로 함.
- Modifier.Node에서 배치·좌표 변화만 관찰함. 좌표를 Compose 상태로 올리지 않으며 항목별 반복 검사·코루틴을 만들지 않음.
- 인자 변경 시 새 배치가 끝난 뒤 1회 재판정함. extras/콜백만 바뀌면 참조만 갱신함.

### 4. Search - 검색 이벤트 트래킹

사용자가 검색을 수행했을 때 사용합니다.

```kotlin
import com.nexters.boolti.common.tracker.event.search

AppTracker.search(
    screen = Screen.Home,
    keyword = "밴드 콘서트",
    properties = mapOf(
        "result_count" to 15
    )
)
```

### 5. Complete - 완료 이벤트 트래킹

특정 작업이 완료되었을 때 사용합니다.

```kotlin
import com.nexters.boolti.common.tracker.event.complete

// "Complete Payment" 이벤트로 기록됨
AppTracker.complete("Payment")

// "Complete Payment" 이벤트로 기록됨 (prefix 자동 제거)
AppTracker.complete("Complete Payment", trimPrefix = true)

// 추가 속성과 함께
AppTracker.complete(
    target = "Reservation",
    properties = mapOf(
        "show_id" to "123",
        "ticket_count" to 2,
        "total_price" to 50000
    )
)
```

### 6. 커스텀 이벤트

기본 제공 이벤트 외에 커스텀 이벤트를 트래킹할 수 있습니다.

```kotlin
AppTracker.trackEvent(
    eventName = "Custom Event",
    properties = mapOf(
        "custom_field" to "value"
    )
)
```

## 사용자 식별

### 로그인 시 사용자 식별

```kotlin
// 기본 사용자 ID만 설정
AppTracker.identify("user_123")

// 사용자 ID와 프로필 정보 함께 설정
AppTracker.identify(
    userId = "user_123",
    properties = mapOf(
        "email" to "user@example.com",
        "name" to "홍길동",
        "signup_date" to "2024-01-01"
    )
)
```

### 사용자 프로필 업데이트

```kotlin
// 기존 userId 유지하면서 프로필만 업데이트
AppTracker.identify(
    properties = mapOf(
        "phone" to "010-1234-5678",
        "marketing_agree" to true
    )
)
```

### 로그아웃

```kotlin
AppTracker.logout()
```

## 고급 기능

### 1. 실행 시간 측정

특정 작업의 실행 시간을 자동으로 측정하고 트래킹합니다.

```kotlin
val result = AppTracker.withTrackTime("Image Upload") {
    // 실행 시간을 측정할 작업
    uploadImage(file)
}
// "Image Upload" 이벤트에 duration 속성이 자동으로 추가됨
```

### 2. 즉시 전송 (Flush)

기본적으로 이벤트는 60초마다 또는 `flushBatchSize`만큼 쌓였을 때 서버로 전송됩니다. 즉시 전송이 필요한 경우:

```kotlin
AppTracker.flush()
```

### 3. Logcat 출력 비활성화

디버깅용 로그 출력을 비활성화할 수 있습니다.

```kotlin
AppTracker.view(
    screen = Screen.Home,
    withLogcat = false // Logcat 출력 안 함
)
```

## 예시 코드

### Composable에서 화면 진입 트래킹

```kotlin
@Composable
fun HomeScreen() {
    LaunchedEffect(Unit) {
        AppTracker.view(Screen.Home)
    }

    // UI 구현...
}
```

### ViewModel에서 이벤트 트래킹

```kotlin
class PaymentViewModel @Inject constructor() : ViewModel() {

    fun onPaymentButtonClick() {
        AppTracker.click(
            screen = Screen.Payment,
            objectRole = Role.Button,
            objectValue = "결제하기",
            properties = mapOf(
                "payment_method" to "card",
                "amount" to 50000
            )
        )

        // 결제 로직...
    }

    fun onPaymentSuccess() {
        AppTracker.complete(
            target = "Payment",
            properties = mapOf(
                "transaction_id" to "tx_123456",
                "amount" to 50000
            )
        )
    }
}
```

### 로그인 플로우 예시

```kotlin
class LoginViewModel @Inject constructor() : ViewModel() {

    fun onLoginSuccess(userId: String, user: User) {
        AppTracker.identify(
            userId = userId,
            properties = mapOf(
                "email" to user.email,
                "name" to user.name,
                "login_type" to "kakao"
            )
        )

        AppTracker.complete("Login")
    }
}

class LogoutViewModel @Inject constructor() : ViewModel() {

    fun onLogout() {
        AppTracker.logout()
    }
}
```

## Screen 추가 방법

새로운 화면을 추가할 때는 `Screen.kt` 파일에 확장 속성을 추가합니다:

```kotlin
// Screen.kt
val Screen.Companion.NewScreen
    get() = Screen("NewScreen")

// 사용
AppTracker.view(Screen.NewScreen)
```

## Role 추가 방법

새로운 UI 요소 타입을 추가할 때는 `Role.kt` 파일에 확장 속성을 추가합니다:

```kotlin
// Role.kt
val Role.Companion.Card
    get() = Role("Card")

// 사용
AppTracker.click(
    screen = Screen.Home,
    objectRole = Role.Card,
    objectValue = "콘서트 카드"
)
```

## 주의사항

### 1. 초기화 순서

- **Timber를 AppTracker보다 먼저 초기화**해야 합니다.
- Application의 `onCreate()`에서 초기화하지 않으면 `UninitializedPropertyAccessException`이 발생합니다.

### 2. withTrackTime 사용 시 예외 처리

`withTrackTime` 블록에서 예외가 발생하면 이벤트가 트래킹되지 않을 수 있습니다. 중요한 트래킹의 경우 별도 예외 처리를 추가하세요.

### 3. 개인정보 주의

사용자 식별 정보나 이벤트 속성에 민감한 개인정보(비밀번호, 주민등록번호 등)를 포함하지 마세요.

### 4. 이벤트 네이밍 규칙

- Screen 이름: PascalCase (예: `"HomeScreen"` → `"Home"`)
- Event 이름: 첫 글자 대문자 (예: `"Click"`, `"View Screen"`)
- Property 키: snake_case (예: `"show_id"`, `"object_role"`)

### 5. 스레드 안전성

여러 스레드에서 동시에 이벤트를 트래킹할 수 있습니다. Mixpanel SDK가 내부적으로 스레드 안전성을 보장합니다.

### 6. 디버깅

개발 중에는 Logcat에서 `AppTracker` 태그로 필터링하여 트래킹 이벤트를 확인할 수 있습니다:

```
adb logcat -s AppTracker
```

로그 형식:
```
(2024-12-07T10:30:45.123) [Click]
[
  "screen: Home",
  "object_role: Button",
  "object_value: 검색"
]
```
