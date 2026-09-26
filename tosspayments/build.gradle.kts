import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("boolti.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.nexters.boolti.tosspayments"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField("String", "TOSS_CLIENT_KEY", localProperty("DEV_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", localProperty("DEV_TOSS_SECRET_KEY"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "TOSS_CLIENT_KEY", localProperty("PROD_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", localProperty("PROD_TOSS_SECRET_KEY"))
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.targetJvm.get()))
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.bundles.lifecycle)
    api(libs.payments.toss)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(libs.mixpanel.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
