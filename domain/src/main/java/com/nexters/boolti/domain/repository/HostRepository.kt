package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.request.QrScanRequest
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    fun requestEntrance(
        request: QrScanRequest,
    ): Flow<Boolean>
}
