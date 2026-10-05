plugins {
    id("boolti.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.nexters.boolti.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "APP_VERSION", "\"${libs.versions.versionName.get()}\"")
        buildConfigField("String", "YOUTUBE_API_KEY", localProperty("YOUTUBE_API_KEY"))
    }

    buildTypes {
        release {
            buildConfigField("String", "BASE_URL", localProperty("PROD_BASE_URL"))
        }
        debug {
            buildConfigField("String", "BASE_URL", localProperty("DEV_BASE_URL"))
            // QA 디버그 정보 전송용. 없으면 전송 버튼이 비활성화된다
            buildConfigField("String", "DISCORD_DEBUG_INFO_WEBHOOK_URL", localPropertyOrNull("DISCORD_DEBUG_INFO_WEBHOOK_URL") ?: "\"\"")
        }
    }
    buildFeatures {
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
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.common.tracker)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.bundles.db)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.bundles.coroutines)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(libs.timber)

    implementation(libs.bundles.network)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.bundles.firebase)

    testImplementation(libs.junit)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.android.test)
}
