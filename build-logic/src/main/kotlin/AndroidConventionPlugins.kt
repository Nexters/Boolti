import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        extensions.configure<ApplicationExtension> {
            configureAndroidCommon(this)
            defaultConfig.targetSdk = version("targetSdk").toInt()
        }
    }
}

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        extensions.configure<LibraryExtension> { configureAndroidCommon(this) }
    }
}

private fun Project.configureAndroidCommon(android: CommonExtension) = with(android) {
    compileSdk = version("compileSdk").toInt()
    defaultConfig.minSdk = version("minSdk").toInt()
    val javaVersion = JavaVersion.toVersion(version("targetJvm"))
    compileOptions.sourceCompatibility = javaVersion
    compileOptions.targetCompatibility = javaVersion
}

private fun Project.version(name: String): String =
    extensions.getByType<VersionCatalogsExtension>().named("libs").findVersion(name).get().requiredVersion
