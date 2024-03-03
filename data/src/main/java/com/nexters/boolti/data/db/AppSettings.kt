package com.nexters.boolti.data.db

import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
internal data class AppSettings(
    val userId: String? = null,
    val loginType: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val photo: String? = null,
    val accessToken: String = "",
    val refreshToken: String = "",
    val refundPolicy: List<String> = emptyList(),
)

internal object AppSettingsSerializer : Serializer<AppSettings> {

    override val defaultValue: AppSettings = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (exception: SerializationException) {
            exception.printStackTrace()
            defaultValue
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t,
            ).encodeToByteArray(),
        )
    }
}
