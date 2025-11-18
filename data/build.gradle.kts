import java.io.FileInputStream
import java.util.Properties

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.nexters.boolti.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "APP_VERSION", "\"${libs.versions.versionName.get()}\"")
        buildConfigField("String", "YOUTUBE_API_KEY", getApiKey("YOUTUBE_API_KEY"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", getApiKey("PROD_BASE_URL"))
        }
        debug {
            buildConfigField("String", "BASE_URL", getApiKey("DEV_BASE_URL"))
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
        buildConfig = true
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.bundles.db)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.bundles.coroutines)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.timber)

    implementation(libs.bundles.network)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.bundles.firebase)

    testImplementation(libs.junit)
    testImplementation(libs.bundles.kotest)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.kotest.runner.junit5.jvm)
}

fun getApiKey(propertyKey: String): String {
    return providers.gradleProperty(propertyKey).orNull ?: localProperties.getProperty(propertyKey)
}
