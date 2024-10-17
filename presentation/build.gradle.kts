import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.nexters.boolti.presentation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "PACKAGE_NAME", "\"${libs.versions.packageName.get()}\"")
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
        buildConfigField("String", "DEV_SUBDOMAIN", getLocalProperty("DEV_SUBDOMAIN"))
    }

    buildTypes {
        debug {
            buildConfigField("String", "TOSS_CLIENT_KEY", getLocalProperty("DEV_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", getLocalProperty("DEV_TOSS_SECRET_KEY"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("String", "TOSS_CLIENT_KEY", getLocalProperty("PROD_TOSS_CLIENT_KEY"))
            buildConfigField("String", "TOSS_SECRET_KEY", getLocalProperty("PROD_TOSS_SECRET_KEY"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.targetJvm.get()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.tosspayments)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.material)
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
    kapt(libs.hilt.compiler)

    implementation(libs.lottie)
    implementation(libs.bundles.coil)
    api(libs.kakao.login)
    implementation(libs.kakao.share)

    implementation(libs.timber)
    implementation(libs.zxing.android.embedded)

    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.andoridx.compose.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    androidTestImplementation(libs.kotest.runner.junit5.jvm)
    testImplementation(libs.junit)
    testImplementation(libs.bundles.kotest)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}

fun getLocalProperty(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}
