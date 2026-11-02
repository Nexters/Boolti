## 선물 딥링크 처리 플로우
1. 앱링크를 통해 진입 (cold start | warm start)
2. 최상위 컴포저블(Main.kt) 에서 GiftDeepLinkViewModel::pendGift 호출 후 홈 화면(HomeScreen.kt)으로 이동
3. 홈 화면에서 GiftDeepLinkViewModel의 선물 이벤트 수신 후 처리
   1. 사전 질문이 없는 공연의 티켓일 경우 다이얼로그 후 선물 받기
   2. 사전 질문이 있는 공연의 티켓일 경우 사전 질문 작성 화면(GiftPreQuestionScreen.kt)으로 이동
4. (3-ii 에서 이어짐) 사전 질문 작성 완료 후 1) 선물 등록 API 호출 2) 사전 질문 등록 API 호출 

## 히스토리
- 3번에서 HomeViewModel의 event를 MutableSharedFlow로 작성할 경우 이벤트를 유실하는 문제가 있어 Channel로 변경
- 4번 과정에서 1번만 성공하는 경우 사전질문이 비어 있는 상태로 선물을 받게 됨.
   - 두 개의 API를 통합하여 백엔드에서 트랜잭션을 처리하는 게 올바른 방법이나, 공수가 많이 든다고 함.
   - 대신 2번 API 실패 시 두 번 더 시도하여 보완함.
   - 기존에는 2번 호출 후 1번을 호출하는 flow였으나 1 -> 2 가 보안적(사전 질문 등록할 때 티켓 소유자 인증 문제)으로 안전하다고 판단하여 변경.
