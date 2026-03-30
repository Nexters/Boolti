package com.nexters.boolti.data.repository

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nexters.boolti.data.datasource.GiftDataSource
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.toDomains
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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    private val dataSource: GiftDataSource,
) : GiftRepository {
    private data class CacheEntry<T>(val value: T, val cachedAt: Long)

    private val giftCache = ConcurrentHashMap<String, CacheEntry<Gift>>()
    private val cacheTtlMs = 60_000L
    override fun receiveGift(giftUuid: String): Flow<Boolean> = flow {
        emit(dataSource.receiveGift(GiftReceiveRequest(giftUuid)))
    }

    override fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment> = flow {
        emit(dataSource.approveGiftPayment(request).toDomain())
    }

    override fun sendFreeGift(request: FreeGiftRequest): Flow<ApproveGiftPayment> = flow {
        emit(dataSource.createFreeGift(request).toDomain())
    }

    override fun getGift(giftUuid: String): Flow<Gift> = flow {
        val cached = giftCache[giftUuid]
        if (cached != null && System.currentTimeMillis() - cached.cachedAt < cacheTtlMs) {
            emit(cached.value)
            return@flow
        }
        val gift = dataSource.getGift(giftUuid).toDomain()
        giftCache[giftUuid] = CacheEntry(gift, System.currentTimeMillis())
        emit(gift)
    }

    override fun getGiftImages(): Flow<List<ImagePair>> = flow {
        emit(dataSource.getGiftImages().toDomains())
    }

    override fun getGiftPaymentInfo(giftId: String): Flow<ReservationDetail> = flow {
        runCatching {
            dataSource.getGiftPaymentInfo(giftId)
        }.onSuccess {
            emit(it.toDomain())
        }.onFailure {
            Firebase.crashlytics.recordException(it)
            Timber.e(it)
        }
    }

    override fun cancelGift(giftUuid: String): Flow<Boolean> = flow {
        emit(dataSource.cancelGift(giftUuid))
    }

    override fun cancelRegisteredGift(giftUuid: String): Flow<Boolean> = flow {
        emit(dataSource.cancelRegisteredGift(giftUuid))
    }
}
