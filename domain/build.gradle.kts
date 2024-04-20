plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    reports {
        junitXml.required.set(false)
    }
    systemProperty("gradle.build.dir", project.buildDir)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
}
