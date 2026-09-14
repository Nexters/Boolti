package com.nexters.boolti.presentation.screen.debug.impression

internal enum class ImpressionDebugCase(
    val title: String,
    val guide: String,
    val code: String,
) {
    Basic(
        "기본 콜백",
        "state와 key를 생략하는 가장 간단한 사용법이에요. 아래 카드 영역을 스크롤해 보세요. 다시 노출되면 횟수가 늘고, 콜백에는 extras만 전달돼요.",
        """
        Modifier.impression(
            extras = { mapOf("sample" to "1") },
        ) { extras ->
            // 기본 key는 자동 UUID
            // 여기서는 extras만 수신
            AppTracker.impression(
                screen = Screen.DebugImpression,
                objectRole = Role.Item,
                objectValue = "ImpressionSample",
                properties = extras,
            )
        }
        """.trimIndent(),
    ),
    Shared(
        "공유 상태",
        "여러 카드에 같은 rememberImpressionState를 전달해요. 숫자 key로 카드를 구분하고, 한 콜백에서 key·threshold·extras를 받아요. 다시 노출되면 재기록돼요.",
        """
        val state = rememberImpressionState { event ->
            // event.key, threshold, extras
            AppTracker.impression(
                event = event,
                screen = Screen.DebugImpression,
                objectRole = Role.Item,
                objectValue = "ImpressionSample",
            )
        }
        Modifier.impression(key = 1, state = state)
        Modifier.impression(key = 2, state = state)
        """.trimIndent(),
    ),
    ViewModel(
        "ViewModel",
        "ViewModel에서 만든 상태를 공유해요. 이 예제는 key별 1회만 기록해요. 화면을 회전해도 상태와 횟수가 유지돼요. 프로세스 종료 후 복원은 별도 구현이 필요해요.",
        """
        class SampleViewModel : ViewModel() {
            val impressionState = ImpressionState(
                deduplicate = true,
                onImpressed = ::trackImpression,
            )
        }
        // Composable에서 전달
        Modifier.impression(
            key = 1,
            state = viewModel.impressionState,
        )
        """.trimIndent(),
    ),
    Once(
        "중복 방지",
        "deduplicate=true를 지정한 rememberImpressionState예요. 스크롤로 나갔다 돌아와도 같은 key는 1회만 기록돼요. 새 세션을 누르면 이력이 초기화돼요.",
        """
        val state = rememberImpressionState(
            deduplicate = true,
            onImpressed = ::trackImpression,
        )
        Modifier.impression(key = 1, state = state)
        // 다시 노출되어도 key=1은 추가 기록 없음
        """.trimIndent(),
    ),
    Threshold(
        "노출 기준",
        "0·25·50·100%를 비교해 보세요. 0%는 화면 밖이어도 실제 배치되면 발생해요. 100%는 카드 전체가 보여야 해요. 기준을 바꾸면 새 세션으로 시작해요.",
        """
        Modifier.impression(
            key = 1,
            state = state,
            threshold = 0.5f,
        )
        // 0f: 실제 배치 시 발생
        // 0.25f: 25% 이상
        // 0.5f: 50% 이상 (기본값)
        // 1f: 전체 노출
        """.trimIndent(),
    ),
    Padding(
        "padding 순서",
        "왼쪽은 여백을 포함하고 오른쪽은 카드 본문만 측정해요. 두 카드를 천천히 스크롤하면 서로 다른 시점에 발생해요. 바깥 회색 영역은 카드 아래쪽 96dp 여백이에요.",
        """
        // 왼쪽: padding 포함 (전체 216dp)
        Modifier
            .impression(key = 1, state = state)
            .padding(bottom = 96.dp)

        // 오른쪽: padding 제외 (본문 120dp)
        Modifier
            .padding(bottom = 96.dp)
            .impression(key = 2, state = state)
        """.trimIndent(),
    ),
    Reset(
        "이력 초기화",
        "key별 1회 상태에서 reset을 시험해요. 이력 삭제 직후에는 재기록되지 않아요. 카드를 스크롤 밖으로 보냈다가 돌아오면 횟수가 늘어요. 화면의 누적 횟수는 유지돼요.",
        """
        val state = rememberImpressionState(
            deduplicate = true,
            onImpressed = ::trackImpression,
        )
        state.reset(1)   // 1번 key 이력만 삭제
        state.resetAll() // 모든 key 이력 삭제
        // 현재 노출 구간은 유지
        // 다음 재노출부터 다시 기록
        """.trimIndent(),
    ),
}
