# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Boolti is an Android application for band concert hosting, booking, and QR entry management. It's a multi-module Android project using Jetpack Compose, Hilt for dependency injection, and a clean architecture pattern.

**Links:**
- Play Store: https://play.google.com/store/apps/details?id=com.nexters.boolti
- App Store: https://apps.apple.com/kr/app/%EB%B6%88%ED%8B%B0/id6476589322
- Host web: https://boolti.in

## API Documentation

API 개발 시 Swagger 문서를 참조하여 스펙을 파악합니다.

- **Swagger URL**: https://dev.api.boolti.in/v3/api-docs/app
- **인증 방식**: Bearer Token (bearerAuth)

**주요 API 카테고리:**
| 카테고리 | Base Path | 설명 |
|---------|-----------|------|
| 인증 | `/app/papi/v1/login/*` | 카카오/애플 로그인, 토큰 갱신 |
| 유저 | `/app/api/v1/user*` | 프로필 조회/수정, 회원탈퇴 |
| 공연 | `/app/papi/v1/show*` | 공연 검색, 상세 조회 |
| 예약 | `/app/api/v1/reservation*` | 예약 목록/상세 |
| 선물 | `/app/api/v1/order/*gift*` | 선물 수령/결제/취소 |
| 결제 | `/app/api/v1/order/*payment*` | 결제 승인/취소 |

**API 개발 시 참고사항:**
- 새 API 구현 전 Swagger 스펙을 먼저 확인
- Request/Response DTO는 스펙과 일치하도록 작성
- `papi` 경로는 Public API (인증 불필요), `api` 경로는 인증 필요

## Module Architecture

The project follows a modular clean architecture with clear separation of concerns:

```
app/              - Main application module, DI setup
domain/           - Business logic, use cases, repositories (interfaces) — pure Kotlin
data/             - Data layer, API services, repositories (implementations)
presentation/     - UI layer with Jetpack Compose
tosspayments/     - Payment integration module
common/logger/    - Common logging utilities
common/tracker/   - Common analytics tracking
```

**Key architectural patterns:**
- **Clean Architecture**: Domain-driven design with clear dependency inversion
- **MVVM**: ViewModels with StateFlow for UI state management
- **Repository Pattern**: Data abstraction between domain and data layers
- **Dependency Injection**: Hilt for DI throughout all modules

## Common Development Commands

### Building and Testing
```bash
# Run all module tests
./gradlew btTest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run specific module tests
./gradlew domain:test
./gradlew data:testDebugUnitTest
./gradlew presentation:testDebugUnitTest

# Clean build
./gradlew clean
```

### Key Build Features
- **Custom APK naming**: Includes version, git hash, and timestamp
- **BuildConfig secrets**: API keys loaded from `local.properties`
- **Multi-environment support**: Debug/Release with different API endpoints
- **Kotest**: Test framework configured across modules

## Development Setup Requirements

### Required Files (Not in Git)
- `local.properties` - Contains API keys and environment configs:
  ```
  KAKAO_APP_KEY="your_key"
  DEV_BASE_URL="dev_api_url"
  PROD_BASE_URL="prod_api_url"
  DEV_TOSS_CLIENT_KEY="dev_toss_key"
  PROD_TOSS_CLIENT_KEY="prod_toss_key"
  ```
- `keystore.properties` - Release signing configuration

### Technology Stack
- **Language**: Kotlin 2.3.10
- **UI**: Jetpack Compose with Material3 (Compose BOM 2026.02.00)
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp with Kotlinx Serialization
- **Database**: Room
- **State Management**: StateFlow, Compose State
- **Testing**: Kotest, MockK
- **Build**: Gradle with Version Catalogs (`gradle/libs.versions.toml`)

## Code Organization Patterns

### Domain Module
- `model/` - Domain entities and value objects
- `repository/` - Repository interfaces
- `usecase/` - Business logic use cases
- `exception/` - Domain-specific exceptions

### Data Module
- `datasource/` - Data source interfaces and implementations
- `network/api/` - Retrofit service interfaces
- `network/request/` - Request DTOs
- `network/response/` - Response DTOs
- `repository/` - Repository implementations
- `db/` - Room database and DataStore

### Presentation Module
- `screen/` - Feature-based screen organization
- `component/` - Reusable UI components
- `navigation/` - Navigation setup and routes
- `theme/` - Design system (colors, typography, dimensions)

### Key Conventions
- **Feature-based packaging**: Each feature in its own package
- **State management**: UiState data classes with sealed class events
- **Navigation**: Type-safe navigation with route objects
- **Dependency flow**: domain ← data, presentation → domain

## Testing Strategy

- **Domain tests**: Pure unit tests with Kotest
- **Data tests**: Repository and API integration tests
- **Presentation tests**: ViewModel and UI component tests
- **Test configuration**: JUnit Platform with Kotest runner

## Firebase Integration

- **Analytics**: User behavior tracking
- **Crashlytics**: Crash reporting
- **Cloud Messaging**: Push notifications
- **Remote Config**: Feature flags and dynamic configuration
- **App Distribution**: 테스터 배포
  - Project ID: `boolti-9a521`
  - Debug App ID: `1:965765235527:android:fd491b0a5869fc69d30262`
  - 테스터 그룹: `안드폰-사용자들`

## Project-local Skills

프로젝트 전용 스킬과 슬래시 커맨드는 `.claude/skills/`, `.claude/commands/`에 위치한다. 자동 로드되므로 별도 설정 불필요.