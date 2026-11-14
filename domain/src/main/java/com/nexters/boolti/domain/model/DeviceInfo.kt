package com.nexters.boolti.domain.model

/**
 * 기기 정보와 현재 상태. 읽지 못한 값은 null
 */
data class DeviceInfo(
    val appVersion: String?,
    val model: String,
    val os: String,
    val locale: String,
    val timezone: String,
    val buildType: String,
    val screen: String?,
    val storageFree: String?,
    val memory: String?,
    val battery: String?,
)
