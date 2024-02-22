plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.material)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose)
    implementation(platform(libs.andoridx.compose.compose.bom))
    implementation(libs.bundles.coroutines)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material3.android)
    implementation(libs.zoomable)
    kapt(libs.hilt.compiler)

    implementation(libs.lottie)
    implementation(libs.bundles.coil)
    api(libs.kakao.login)

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
