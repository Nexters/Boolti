package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.ApproveGiftPayment
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.request.GiftApproveRequest
import kotlinx.coroutines.flow.Flow

interface GiftRepository {
    fun receiveGift(giftUuid: String): Flow<Boolean>

    fun approveGiftPayment(request: GiftApproveRequest): Flow<ApproveGiftPayment>

    fun getGift(giftUuid: String): Flow<Gift>

    fun getGiftImages(): Flow<List<ImagePair>>
}