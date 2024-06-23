package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.ApproveGiftPayment
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.request.GiftApproveRequest
import kotlinx.coroutines.flow.Flow

interface GiftRepository {
    fun receiverGift(giftId: String): Flow<Boolean>

    fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment>

    fun getGift(giftId: String): Flow<Gift>

    fun getGiftImages(): Flow<List<ImagePair>>
}