---
paths:
  - "data/**"
  - "domain/**"
---

# Data · Domain 규칙

## 패키지

- domain: `model/`, `repository/`(인터페이스), `usecase/`, `exception/`, `util/`
- data: `datasource/`, `network/api/`(Retrofit), `network/request/`, `network/response/`, `repository/`(구현), `db/`(Room, DataStore), `di/`

## 레이어

- domain에 `@Serializable`, 요청·응답 DTO를 두지 않는다 (`domain/request/`의 기존 DTO는 data로 옮길 예정)
- Repository에서 단건 조회는 `suspend fun xxx(): Result<T>`, 계속 바뀌는 값은 `Flow<T>`로 반환한다
- `runCatching` 대신 `suspendRunCatching`(`domain/util`)을 쓴다. CI가 data 레이어의 `runCatching`을 막는다
  - 현재 코루틴이 취소된 경우에만 취소 예외를 다시 던진다
  - 안쪽 `withTimeout` 타임아웃, 다른 코루틴의 취소 예외는 `Result.failure`로 받는다
- UseCase는 여러 Repository를 조합할 때만 만든다. 이름은 `XxxUseCase`
- DTO 프로퍼티에는 모두 `@SerialName`을 붙인다

## API

- 스펙은 Swagger에서 먼저 확인한다: https://dev.api.boolti.in/v3/api-docs/app
- `papi` 경로는 인증 불필요, `api` 경로는 Bearer 토큰 필요
- Request/Response DTO는 스펙과 일치시킨다

## 재시도

- 호출하는 쪽에서 재시도하지 않는다
- `RetryInterceptor`가 GET 요청의 5xx 응답을 지수적 백오프로 재시도한다 (최대 3회, 0.5초 → 1초 → 2초)
- 재시도하면 안 되는 GET은 API 메서드에 `@DoNotRetry`를 붙인다
- POST·PUT 등은 재시도하지 않는다
