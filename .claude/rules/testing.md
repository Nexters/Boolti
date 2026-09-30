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
- ViewModel·Repository에서 `Dispatchers.IO`/`Default`를 직접 써야 하면, 디스패처를 생성자로 주입받아 테스트에서 바꿀 수 있게 한다
- Repository 목(mock)은 `coEvery { ... } returns Result.success(...)` / `Result.failure(...)`로 반환한다
