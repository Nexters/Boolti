import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("boolti.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.nexters.boolti.presentation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "PACKAGE_NAME", "\"${libs.versions.packageName.get()}\"")
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "TOSS_CLIENT_KEY", localProperty("DEV_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", localProperty("DEV_TOSS_SECRET_KEY"))
            buildConfigField("String", "DOMAIN", localProperty("DEV_DOMAIN"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "TOSS_CLIENT_KEY", localProperty("PROD_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", localProperty("PROD_TOSS_SECRET_KEY"))
            buildConfigField("String", "DOMAIN", localProperty("PROD_DOMAIN"))
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.targetJvm.get()))
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.tosspayments)
    implementation(projects.common.tracker)
    implementation(projects.common.logger)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.material)
    implementation(libs.material.icons.extended)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose)
    implementation(libs.immutable)
    implementation(platform(libs.andoridx.compose.compose.bom))
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.firebase.bom))

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material3.android)
    implementation(libs.zoomable)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(libs.lottie)
    implementation(libs.bundles.coil)
    api(libs.kakao.login)
    implementation(libs.kakao.share)

    implementation(libs.timber)
    implementation(libs.zxing.android.embedded)
    implementation(libs.reorderable)

    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.andoridx.compose.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.kotest.runner.junit5.jvm)
    testImplementation(libs.junit)
    testImplementation(libs.bundles.kotest)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}
