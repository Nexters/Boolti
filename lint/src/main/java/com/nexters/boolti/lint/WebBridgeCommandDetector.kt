package com.nexters.boolti.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.skipParenthesizedExprDown

/**
 * `WebBridge.handle`에 `@WebBridgeCommand`가 없는 타입을 넘기면 에러로 표시한다.
 *
 * 커맨드 클래스(다른 파일)를 고치는 빠른 수정은 Android Studio에서 적용되지 않아, 붙일 어노테이션을 메시지로 알려준다.
 */
class WebBridgeCommandDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("handle")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (method.containingClass?.qualifiedName != WEB_BRIDGE) return

        val lambdaParameter = (node.valueArguments.lastOrNull()?.skipParenthesizedExprDown() as? ULambdaExpression)
            ?.valueParameters
            ?.firstOrNull()
        val commandType = node.typeArguments.firstOrNull() ?: lambdaParameter?.type
        val commandClass = (commandType as? PsiClassType)?.resolve() ?: return
        if (commandClass.hasAnnotation(WEB_BRIDGE_COMMAND)) return
        val name = commandName(commandClass.name.orEmpty())

        context.report(
            issue = ISSUE,
            scope = node,
            location = lambdaParameter?.let { context.getLocation(it as UElement) } ?: context.getCallLocation(node, false, false),
            message = "${commandClass.name}에 `@WebBridgeCommand(\"$name\")`를 붙여주세요. " +
                "이름이 웹 커맨드와 같은지도 확인해 주세요.",
        )
    }

    companion object {
        private const val WEB_BRIDGE = "com.nexters.boolti.presentation.util.bridge.WebBridge"
        private const val WEB_BRIDGE_COMMAND = "com.nexters.boolti.presentation.util.bridge.WebBridgeCommand"

        /** `ShowToast` → `SHOW_TOAST` */
        internal fun commandName(className: String): String =
            className.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2").uppercase()

        val ISSUE = Issue.create(
            id = "WebBridgeCommandMissing",
            briefDescription = "@WebBridgeCommand가 없는 웹 브릿지 커맨드",
            explanation = """
                `WebBridge.handle`에 넘기는 타입에는 `@WebBridgeCommand("커맨드 이름")`이 있어야 해요.
                없으면 화면을 여는 순간 등록 단계에서 앱이 종료돼요.
            """,
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.FATAL,
            implementation = Implementation(WebBridgeCommandDetector::class.java, Scope.JAVA_FILE_SCOPE),
        )
    }
}
