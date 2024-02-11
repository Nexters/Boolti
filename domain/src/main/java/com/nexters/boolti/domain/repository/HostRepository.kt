package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.request.QrScanRequest
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    fun requestEntrance(
        request: QrScanRequest,
    ): Flow<Boolean>

    fun getHostedShows(): Flow<List<Show>>
    fun getManagerCode(showId: String): Flow<String>
}
