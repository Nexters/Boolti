package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.HostDataSource
import com.nexters.boolti.data.datasource.TicketDataSource
import com.nexters.boolti.domain.exception.QrErrorType
import com.nexters.boolti.domain.exception.QrScanException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.repository.HostRepository
import com.nexters.boolti.domain.request.QrScanRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HostRepositoryImpl @Inject constructor(
    private val dataSource: HostDataSource,
) : HostRepository {
    override fun requestEntrance(request: QrScanRequest): Flow<Boolean> = flow {
        val response = dataSource.requestEntrance(request)
        if (response.isSuccessful) {
            emit(response.body() ?: false)
        } else {
            val errMsg = response.errorBody()?.string()
            throw QrScanException(QrErrorType.fromString(errMsg?.errorType))
        }
    }

    override fun getHostedShows(): Flow<List<Show>> = flow {
        emit(dataSource.getHostedShows())
    }
}
