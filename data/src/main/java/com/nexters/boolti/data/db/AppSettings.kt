package com.nexters.boolti.data.db

import androidx.datastore.core.Serializer
import com.nexters.boolti.data.network.response.ShowResponse
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate

@Serializable
internal data class AppSettings(
    val userId: String? = null,
    val loginType: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val photo: String? = null,
    val userCode: String? = null,
    val performedShow: List<ShowResponse> = emptyList(),
    val profileIntroduction: String = "",
    val profileLink: List<EditProfileRequest.LinkDto> = emptyList(),
    val profileSns: List<EditProfileRequest.SnsDto> = emptyList(),
    val accessToken: String = "",
    val refreshToken: String = "",
    val refundPolicy: List<String> = emptyList(),
    val dateHidingEvent: String? = null,
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
