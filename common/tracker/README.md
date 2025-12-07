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
    properties = mapOf(
        "banner_id" to "promotion_001"
    )
)
```

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
