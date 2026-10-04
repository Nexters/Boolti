package com.nexters.boolti.data.repository

import com.nexters.boolti.data.cache.CacheKeys
import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.datasource.GiftDataSource
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.toDomains
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.ApproveGiftPayment
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.request.FreeGiftRequest
import com.nexters.boolti.domain.request.GiftApproveRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    private val dataSource: GiftDataSource,
    private val cacheStore: CacheStore,
) : GiftRepository {
    override fun receiveGift(giftUuid: String): Flow<Boolean> = flow {
        val received = dataSource.receiveGift(GiftReceiveRequest(giftUuid))
        cacheStore.invalidate(CacheKeys.gift(giftUuid))
        emit(received)
    }

    override fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment> = flow {
        val response = dataSource.approveGiftPayment(request)
        if (response.isSuccessful) {
            val body = response.body() ?: throw TicketingException(TicketingErrorType.Unknown)
            emit(body.toDomain())
        } else {
            val errMsg = response.errorBody()?.string()
            throw TicketingException(TicketingErrorType.fromString(errMsg?.errorType))
        }
    }

    override fun sendFreeGift(request: FreeGiftRequest): Flow<ApproveGiftPayment> = flow {
        emit(dataSource.createFreeGift(request).toDomain())
    }

    override fun getGift(giftUuid: String): Flow<Gift> = flow {
        emit(cacheStore.getOrFetch(CacheKeys.gift(giftUuid)) { dataSource.getGift(giftUuid).toDomain() })
    }

    override fun getGiftImages(): Flow<List<ImagePair>> = flow {
        emit(cacheStore.getOrFetch(CacheKeys.giftImages) { dataSource.getGiftImages().toDomains() })
    }

    override fun getGiftPaymentInfo(giftId: String): Flow<ReservationDetail> = flow {
        runCatching {
            dataSource.getGiftPaymentInfo(giftId)
        }.onSuccess {
            emit(it.toDomain())
        }.onFailure {
            Timber.e(it)
        }
    }

    override fun cancelGift(giftUuid: String): Flow<Boolean> = flow {
        val canceled = dataSource.cancelGift(giftUuid)
        cacheStore.invalidate(CacheKeys.gift(giftUuid))
        emit(canceled)
    }

    override fun cancelRegisteredGift(giftUuid: String): Flow<Boolean> = flow {
        val canceled = dataSource.cancelRegisteredGift(giftUuid)
        cacheStore.invalidate(CacheKeys.gift(giftUuid))
        emit(canceled)
    }
}
