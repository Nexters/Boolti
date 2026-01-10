package com.nexters.boolti.common.tracker

import android.annotation.SuppressLint
import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.nexters.boolti.common.tracker.AppTracker.identify
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AppTracker {
    @SuppressLint("StaticFieldLeak")
    private lateinit var tracker: MixpanelAPI

    private val logTimeFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault())

    private val currentTime: String
        get() = logTimeFormatter.format(Instant.now())

    private val json = Json { prettyPrint = true }

    /**
     * [AppTracker] 를 초기화합니다.
     *
     * @param context
     */
    fun initialize(
        context: Context,
    ) {
        initialize(context, DEFAULT_FLUSH_BATCH_SIZE)
    }

    /**
     * [AppTracker] 를 초기화합니다.
     *
     * @param context
     * @param flushBatchSize 한번에 전송할 이벤트의 개수
     * @param superProperties 이벤트 전역적으로 설정할 속성들
     */
    fun initialize(
        context: Context,
        flushBatchSize: Int,
        superProperties: Map<String, Any> = emptyMap(),
    ) {
        val superPropertiesJsonObject = JSONObject(superProperties)
        tracker = MixpanelAPI.getInstance(
            /* context = */ context.applicationContext,
            /* token = */ BuildConfig.MIXPANEL_TOKEN,
            /* superProperties = */ superPropertiesJsonObject,
            /* trackAutomaticEvents = */ true,
        ).apply {
            this.flushBatchSize = flushBatchSize
        }

        Timber
            .tag(TAG)
            .i("[AppTracker Initialized] flushBatchSize: $flushBatchSize, superProperties: $superProperties")
    }

    fun trackEvent(
        eventName: String,
        properties: Map<String, Any> = emptyMap(),
        withLogcat: Boolean = true,
    ) {
        val jsonProperties = JSONObject(properties)

        if (properties.isEmpty()) {
            tracker.track(eventName)
        } else {
            tracker.track(eventName, jsonProperties)
        }

        if (withLogcat) {
            trackLogcat(eventName, properties)
        }
    }

    /**
     * [block] 이 실행되는데 걸리는 시간을 트래킹합니다.
     */
    fun <T> withTrackTime(
        eventName: String,
        block: () -> T,
    ) {
        tracker.timeEvent(eventName)
        block().also { tracker.track(eventName) }
    }

    /**
     * 누적된 로그를 즉시 전송합니다.
     *
     * flush 가 불리지 않으면 60초마다 혹은 [MixpanelAPI.getFlushBatchSize] 만큼 채워졌을 때까지 누적하다가 전송합니다.
     *
     */
    fun flush() {
        tracker.flush()
    }

    /**
     * 현재 로그인 한 유저를 식별하기 위한 설정
     */
    fun identify(userId: String) {
        tracker.identify(userId)
    }

    /**
     * 현재 로그인 한 유저의 부가 정보
     *
     * @param userId 유저 식별자. null 이면 기존 [identify] 를 통해 설정된 userId 를 사용합니다.
     * @param properties 유저의 부가 정보. ex) email, name, sns type 등
     */
    fun identify(
        properties: Map<String, Any>,
        userId: String? = null,
        withLogcat: Boolean = true,
    ) {
        val jsonProperties = JSONObject(properties)
        if (userId != null) tracker.identify(userId)
        tracker.people.set(jsonProperties)

        if (withLogcat) trackLogcat(
            "identify",
            buildMap {
                put("userId", userId.orEmpty())
                putAll(properties)
            }
        )
    }

    /**
     * 유저가 로그아웃 한 경우 호출합니다.
     *
     */
    fun logout(
        withLogcat: Boolean = true,
    ) {
        tracker.track("logout")
        tracker.reset()

        if (withLogcat) trackLogcat("logout")
    }

    private fun trackLogcat(
        eventName: String,
        properties: Map<String, Any> = emptyMap(),
    ) {
        val message = json.encodeToString(properties.map { (key, value) -> "$key: $value" })

        Timber
            .tag(TAG)
            .i("($currentTime) [$eventName]\n$message")
    }

    const val DEFAULT_FLUSH_BATCH_SIZE = 50
    private const val TAG = "AppTracker"
}
