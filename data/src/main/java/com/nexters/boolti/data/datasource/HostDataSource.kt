package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.HostService
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.request.QrScanRequest
import retrofit2.Response
import javax.inject.Inject

class HostDataSource @Inject constructor(
    private val apiService: HostService,
) {
    suspend fun requestEntrance(request: QrScanRequest): Response<Boolean> = apiService.requestEntrance(request)
    suspend fun getHostedShows(): List<Show> = apiService.getHostedShows().map { it.toDomain() }
}
