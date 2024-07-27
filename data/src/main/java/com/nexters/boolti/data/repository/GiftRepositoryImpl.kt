package com.nexters.boolti.data.repository

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
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    private val dataSource: GiftDataSource
) : GiftRepository {
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
        emit(dataSource.getGift(giftUuid).toDomain())
    }

    override fun getGiftImages(): Flow<List<ImagePair>> = flow {
        emit(dataSource.getGiftImages().toDomains())
    }

    override fun getGiftPaymentInfo(giftId: String): Flow<ReservationDetail> = flow {
        emit(dataSource.getGiftPaymentInfo(giftId).toDomain())
    }

    override fun cancelGift(giftUuid: String): Flow<Boolean> = flow {
        emit(dataSource.cancelGift(giftUuid))
    }
}