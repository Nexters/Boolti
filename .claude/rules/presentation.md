---
paths:
  - "presentation/**"
  - "tosspayments/**"
---

# Presentation 규칙

## 패키지

- `screen/<기능>/` - 화면, ViewModel, `XxxNavigation.kt`(`NavGraphBuilder.xxxScreen()` 확장 함수)
- `screen/navigation/` - 타입 안전 내비게이션 Route 정의 (`XxxRoute`)
- `component/` - 공통 컴포넌트 (`Bt` 접두사: `BtAppBar`, `BTDialog`, `BtBottomSheet` 등)
- `theme/` - 색상, 타이포, 크기
- `util/` - `ObserveAsEvents`, `SnackbarController` 등

## MVI

- 화면마다 `XxxContract.kt`에 `XxxUiState`, `XxxAction`, `XxxEvent` 세 타입을 둔다
  - `Action`: Screen → ViewModel, 사용자가 한 일
  - `UiState`: ViewModel → Screen, 지금 그릴 상태
  - `Event`: ViewModel → Screen, 이동·스낵바처럼 한 번만 할 일
- ViewModel은 `BaseViewModel` 없이 `ViewModel()`을 직접 상속한다 (`base/BaseViewModel`은 기존 화면에만 남아 있음)
- ViewModel 공개 멤버는 `uiState: StateFlow`, `event: Flow`, `onAction(action)` 세 개뿐이다. 공개 `sendEvent`는 두지 않는다
- 상태는 `_uiState.update { }`로만 바꾼다. 로딩·에러·다이얼로그 상태도 `UiState`에 둔다
- Event는 `Channel(Channel.BUFFERED)`로 보내고, Screen에서 `ObserveAsEvents`로 소비한다. `SharedFlow`는 쓰지 않는다
- Repository 실패는 `Result.onFailure`에서 직접 처리한다

## Screen

- 두 겹으로 나눈다
  - 바깥 `XxxScreen(viewModel = hiltViewModel())`: ViewModel 연결, Event 처리
  - 안쪽 `private XxxScreen(uiState, onAction)`: 그리기만
- `Route` 접미사는 내비게이션 타입에만 쓴다
- Composable 안에서 early return을 쓰지 않는다. 분기는 `when`으로 한다
- Composable이 200줄을 넘거나 나누는 편이 유리하면 컴포넌트로 분리한다
- 사용자에게 보이는 문구는 `strings.xml`에 두고 `stringResource`로 쓴다
- 색상·여백은 `theme/`의 값(`Grey05`~, `Orange01`, `marginHorizontal` 등)을 쓴다. `Color(0xFF...)`를 직접 쓰지 않는다

## 내비게이션

- `navigation<XxxRoot>` 그래프 안에서 `sharedViewModel()`을 쓰는 화면으로 이동하는 클릭은 화면이 RESUMED일 때만 이동한다
  - 인자 없는 콜백: `dropUnlessResumed { navController.navigate(...) }`
  - 인자 있는 콜백: `if (entry.lifecycle.currentState == Lifecycle.State.RESUMED)`로 감싼다
  - 뒤로 가는 중에 이동하면 그래프 밖에서 자식 화면이 열려, 인자 없는 부모 entry의 ViewModel이 생성되다 크래시 난다

## Composable

- UI를 그리는 Composable은 `modifier: Modifier = Modifier`를 받는다 (진입 Screen, `NavGraphBuilder` 확장, Preview는 예외)
- 가시성
  - `screen/` 안의 보조 Composable은 `private`. 여러 화면이 같이 쓰면 `internal`
  - public은 진입 Screen, `NavGraphBuilder` 확장, `component/`의 공통 컴포넌트만
- Preview
  - 본문을 `BooltiTheme { }`로 감싼다
  - 이름은 `{컴포넌트명}Preview`, `private`으로 둔다

## 웹뷰

- `BtWebView`는 기본으로 포커스를 받지 않는다. LazyColumn 안에서 포커스를 가진 WebView가 사라지면(탭 전환, 스크롤) Compose가 크래시 난다
- 키 입력·뒤로가기 키가 필요한 화면만 `BtWebView(context = context, focusable = true)`로 켠다 (예: 공연 등록). 이 WebView는 LazyColumn 안에 넣지 않는다
- `focusable = true` WebView가 탭 안에 필요하면 탭을 바꿔도 화면에서 빼지 않는다
  - WebView는 `when (selectedTab)` 바깥에 두고 URL이나 `visibility`만 바꾼다 (예: 장소 화면)
  - 빼야 한다면 탭 클릭 핸들러에서 상태를 바꾸기 전에 `webView.clearFocus()`를 부른다. `onDispose`에서 부르면 늦다
  - `HorizontalPager`도 화면 밖 페이지를 빼므로 LazyColumn과 같이 피한다

## 웹 브릿지

- 커맨드는 `util/bridge/WebBridgeCommands.kt`에 `@WebBridgeCommand("웹 커맨드 이름")` 데이터 클래스로 정의한다. `@Serializable`은 붙이지 않는다
- 모든 웹뷰 화면에 필요한 커맨드는 `rememberWebBridge` 안에, 한 화면만 쓰는 커맨드는 그 화면의 `rememberWebBridge { handle { ... } }`에 등록한다
- `handle { t: 타입 -> }`의 반환값이 웹 응답 data다 (`Unit`이면 `null`). 실패하면 응답하지 않는다
- 웹뷰를 만들 때 `loadUrl`보다 먼저 `bridge.attach(webView)`를 호출한다

## 실패 표시

- 처음 로딩 실패: 에러 화면 + 다시 시도
- 요청 실패: 스낵바
- 코드 버그: 크래시
