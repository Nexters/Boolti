package com.nexters.boolti.data.db

import androidx.datastore.core.Serializer
import com.nexters.boolti.data.network.response.ShowResponse
import com.nexters.boolti.domain.request.EditProfileRequest
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
    val userCode: String? = null,
    val performedShow: List<ShowResponse> = emptyList(),
    val hasMorePerformedShow: Boolean = false,
    val performedShowCount: Int = 0,
    val showPerformedShow: Boolean = false,
    val profileIntroduction: String = "",
    val profileLink: List<EditProfileRequest.LinkDto> = emptyList(),
    val hasMoreLink: Boolean = false,
    val profileLinkCount: Int = 0,
    val profileSns: List<EditProfileRequest.SnsDto> = emptyList(),
    val upcomingShow: List<ShowResponse> = emptyList(),
    val hasMoreUpcomingShow: Boolean = false,
    val upcomingShowCount: Int = 0,
    val showUpcomingShow: Boolean = false,
    val videoCount: Int = 0,
    val hasMoreVideoItem: Boolean = false,
    val video: List<String> = emptyList(),
    val accessToken: String = "",
    val refreshToken: String = "",
    val refundPolicy: List<String> = emptyList(),
    val dateHidingEvent: Map<String, Long>? = null, // id - date
    val shouldShowNaverMapDialog: Boolean = true,
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
