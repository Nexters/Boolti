plugins {
    id("boolti.android.library")
}

android {
    namespace = "com.nexters.boolti.common.logger"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.timber)
}
