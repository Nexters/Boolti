package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.ApproveGiftPayment
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.request.FreeGiftRequest
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.request.GiftApproveRequest
import kotlinx.coroutines.flow.Flow

interface GiftRepository {
    fun receiveGift(giftUuid: String): Flow<Boolean>

    fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment>

    fun sendFreeGift(request: FreeGiftRequest): Flow<ApproveGiftPayment>

    fun getGift(giftUuid: String): Flow<Gift>

    fun getGiftImages(): Flow<List<ImagePair>>

    fun getGiftPaymentInfo(giftId: String): Flow<ReservationDetail>

    fun cancelGift(giftUuid: String): Flow<Boolean>

    fun cancelRegisteredGift(giftUuid: String): Flow<Boolean>
}