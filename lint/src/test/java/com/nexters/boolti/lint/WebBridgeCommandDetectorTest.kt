package com.nexters.boolti.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class WebBridgeCommandDetectorTest : LintDetectorTest() {

    override fun getDetector(): Detector = WebBridgeCommandDetector()

    override fun getIssues(): List<Issue> = listOf(WebBridgeCommandDetector.ISSUE)

    private val stubs = arrayOf(
        kotlin(
            """
            package kotlinx.serialization

            annotation class Serializable
            """,
        ).indented(),
        kotlin(
            """
            package com.nexters.boolti.presentation.util.bridge

            annotation class WebBridgeCommand(val name: String)

            class WebBridge {
                inline fun <reified T : Any, reified R> handle(noinline block: suspend (T) -> R) {}
            }
            """,
        ).indented(),
    )

    private fun screen(): TestFile = kotlin(
        """
        package test

        import com.nexters.boolti.presentation.util.bridge.WebBridge

        fun register(bridge: WebBridge) {
            bridge.handle { t: ShowToast -> }
        }
        """,
    ).indented()

    fun `test 어노테이션이 있으면 경고하지 않는다`() {
        lint().allowMissingSdk().files(
            *stubs,
            screen(),
            kotlin(
                """
                package test

                import com.nexters.boolti.presentation.util.bridge.WebBridgeCommand

                @WebBridgeCommand("SHOW_TOAST")
                data class ShowToast(val message: String)
                """,
            ).indented(),
        ).run().expectClean()
    }

    fun `test 어노테이션이 없으면 붙일 어노테이션을 알려주는 에러를 낸다`() {
        lint().allowMissingSdk().files(
            *stubs,
            screen(),
            kotlin(
                """
                package test

                import kotlinx.serialization.Serializable

                @Serializable
                data class ShowToast(val message: String)
                """,
            ).indented(),
        ).run().expect(
            """
            src/test/test.kt:6: Error: ShowToast에 `@WebBridgeCommand("SHOW_TOAST")`를 붙여주세요. 이름이 웹 커맨드와 같은지도 확인해 주세요. [WebBridgeCommandMissing]
                bridge.handle { t: ShowToast -> }
                                ~~~~~~~~~~~~
            1 error
            """,
        )
    }

    fun `test 클래스 이름을 대문자 스네이크 표기로 바꿔 이름을 제안한다`() {
        assertEquals("SHOW_TOAST", WebBridgeCommandDetector.commandName("ShowToast"))
        assertEquals("VIEW_PLACE_PHOTO_DETAIL", WebBridgeCommandDetector.commandName("ViewPlacePhotoDetail"))
        assertEquals("REQUEST_TOKEN", WebBridgeCommandDetector.commandName("RequestToken"))
    }
}
