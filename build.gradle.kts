// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kover)
}

kover {
    merge {
        projects(":domain", ":data", ":presentation", ":common:tracker")
        // Android 모듈은 debug만 측정
        createVariant("unit") {
            add("jvm", "debug", optional = true)
        }
    }
    reports {
        filters {
            excludes {
                // UI
                annotatedBy(
                    "androidx.compose.runtime.Composable",
                    "androidx.compose.ui.tooling.preview.Preview",
                )
                packages(
                    "com.nexters.boolti.presentation.component",
                    "com.nexters.boolti.presentation.theme",
                    "com.nexters.boolti.presentation.screen.navigation",
                    "com.nexters.boolti.presentation.screen.debug",
                )
                classes(
                    "*ComposableSingletons*",
                    "*Activity", "*Activity\$*", "*Service", "*Service\$*",
                    "*ScreenKt", "*ScreenKt\$*", "*NavigationKt", "*NavigationKt\$*",
                    "*.AsyncImageBlurModel*", "*.TicketShape*",
                )
                // DI 설정과 Hilt·Room 자동 생성 코드
                packages("com.nexters.boolti.data.di", "hilt_aggregated_deps", "dagger.hilt.internal")
                classes(
                    "Hilt_*", "*.Hilt_*", "*_Factory", "*_Factory\$*", "*_MembersInjector",
                    "*_HiltModules*", "*_GeneratedInjector", "*_Impl", "*_Impl\$*",
                )
                androidGeneratedClasses()
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete = setOf(rootProject.layout.buildDirectory)
}

tasks.register("btTest") {
    dependsOn(":domain:test", ":data:testDebugUnitTest", ":presentation:testDebugUnitTest", ":common:tracker:testDebugUnitTest", ":lint:test")
}
