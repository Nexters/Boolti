import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("boolti.android.library")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nexters.boolti.common.logger"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.targetJvm.get()))
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.timber)
}
