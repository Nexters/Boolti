package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.DeviceInfo

interface DeviceInfoRepository {
    /** 매 요청 헤더에서 동기로 부르므로 suspend가 아니다 */
    fun getDeviceInfo(): DeviceInfo
}
