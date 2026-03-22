import com.android.build.api.artifact.SingleArtifact
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("kotlin-parcelize")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))

android {
    namespace = libs.versions.packageName.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    defaultConfig {
        applicationId = "com.nexters.boolti"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "KAKAO_APP_KEY", getApiKey("KAKAO_APP_KEY"))
        buildConfigField("String", "YOUTUBE_API_KEY", getApiKey("YOUTUBE_API_KEY"))
        manifestPlaceholders["KAKAO_APP_KEY"] = (localProperties["KAKAO_APP_KEY"] as String).trim('"')
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.targetJvm.get()))
    }
}

androidComponents {
    onVariants { variant ->
        val capitalizedName = variant.name.replaceFirstChar { it.uppercase() }
        val apkDir = variant.artifacts.get(SingleArtifact.APK)
        val gitHash = providers.exec {
            commandLine("git", "rev-parse", "--short=7", "HEAD")
            isIgnoreExitValue = true
        }.standardOutput.asText.map { it.trim().ifEmpty { "nogit" } }

        tasks.register("rename${capitalizedName}Apk") {
            doLast {
                val dir = apkDir.get().asFile
                if (!dir.exists()) return@doLast
                val versionName = libs.versions.versionName.get()
                val buildType = variant.buildType ?: "unknown"
                val hash = gitHash.get()
                val date = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                dir.listFiles()?.filter { it.extension == "apk" }?.forEach { apk ->
                    val newName = "app-$buildType-$versionName-$hash-$date.apk"
                    apk.renameTo(File(apk.parentFile, newName))
                }
            }
        }

        tasks.configureEach {
            if (name == "assemble$capitalizedName") {
                finalizedBy("rename${capitalizedName}Apk")
            }
        }
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.presentation)
    implementation(projects.tosspayments)
    implementation(projects.common.tracker)
    implementation(projects.common.logger)

    implementation(libs.bundles.coroutines)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(libs.timber)
    implementation(libs.zxing.android.embedded)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

fun getApiKey(propertyKey: String): String {
    return providers.gradleProperty(propertyKey).orNull ?: localProperties.getProperty(propertyKey)
}
