package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.GiftDataSource
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.toDomains
import com.nexters.boolti.domain.model.ApproveGiftPayment
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.request.GiftApproveRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    private val dataSource: GiftDataSource
) : GiftRepository {
    override fun receiverGift(giftId: String): Flow<Boolean> = flow {
        emit(dataSource.receiverGift(GiftReceiveRequest(giftId)))
    }

    override fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment> = flow {
        emit(dataSource.approveGiftPayment(request).toDomain())
    }

    override fun getGift(giftId: String): Flow<Gift> = flow {
        emit(dataSource.getGift(giftId).toDomain())
    }

    override fun getGiftImages(): Flow<List<ImagePair>> = flow {
        emit(dataSource.getGiftImages().toDomains())
    }
}