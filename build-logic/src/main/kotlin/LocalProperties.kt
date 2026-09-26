import java.util.Properties
import org.gradle.api.Project

/** Gradle property 우선, 없으면 루트 local.properties 에서 읽는다. */
fun Project.localProperty(key: String): String =
    providers.gradleProperty(key).orNull
        ?: Properties().apply {
            providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
                .asText.orNull?.let { load(it.reader()) }
        }.getProperty(key)
        ?: error("'$key' 가 local.properties 에 없어요")
