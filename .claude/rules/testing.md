---
paths:
  - "**/src/test/**"
---

# 테스트 규칙

- Kotest + MockK를 쓴다. 스펙 스타일(`BehaviorSpec`, `DescribeSpec` 등)은 자유
- ViewModel 테스트는 메인 디스패처를 바꿔 끼운다
  ```kotlin
  beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
  afterTest { Dispatchers.resetMain() }
  ```
- Retrofit·Room의 `suspend` 함수는 알아서 백그라운드에서 돌기 때문에, ViewModel이 `viewModelScope`만 쓰면 `setMain`만으로 테스트할 수 있다
- 디스패처를 주입받는 클래스는 `runTest`와 같은 스케줄러의 `StandardTestDispatcher(testScheduler)`를 넘긴다
- 테스트 하나는 한 가지 동작만 검증하고, 이름은 보장하는 동작을 한국어 문장으로 쓴다 (예: `"주문 번호 요청 중에 다시 눌러도 요청은 한 번만 나간다"`)
- 목(mock)은 대상이 생성자로 받는 의존성(Repository, UseCase, API 등)에만 쓴다. 대상 안쪽 로직이나 모델은 목으로 만들지 않는다
  - Repository 목은 `coEvery { ... } returns Result.success(...)` / `Result.failure(...)`로 반환한다
  - `coVerify`로 호출만 확인하는 테스트는 결과·상태로 확인할 수 없을 때만 쓴다 (예: 중복 요청 방지)
- 대상 객체는 생성 함수(`createViewModel(...)`, `dataSource(...)` 등)로 만들고, 목은 각 테스트 안에서 만들어 인자로 넘긴다
  - 목을 스펙 필드로 공유하면 Kotest가 스펙 인스턴스를 테스트끼리 재사용하므로 `beforeTest { clearMocks(...) }`로 초기화한다
- Android 클래스는 Robolectric 없이 처리한다
  - `SavedStateHandle`은 `SavedStateHandle(mapOf(...))`로 직접 만든다
  - `Context`·`ContentResolver`는 `mockk`로 필요한 것만 채운다
  - `Uri.parse` 같은 정적 함수는 로직 분리가 어려울 때만 `mockkStatic`으로 막고 `afterSpec`에서 `unmockkStatic`한다
- 입력만 다르고 검증이 같은 케이스는 `withData`로 묶는다 (Kotest 엔진에 포함, 의존성 추가 불필요)
  ```kotlin
  withData("https://youtu.be/abc", "https://www.youtube.com/embed/abc") { url ->
      YouTubeUrlUtils.extractVideoId(url) shouldBe "abc"
  }
  ```
- 같은 Repository 목 설정이 3개 이상 테스트 파일에서 반복되면 Fake(메모리로 동작하는 구현체)로 바꾼다
  - Fake는 domain 모듈의 `testFixtures`에 둔다. 처음 만들 때 domain에 `java-test-fixtures` 플러그인을 추가한다
