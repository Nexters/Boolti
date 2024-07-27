package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.GiftService
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.ApproveGiftPaymentResponse
import com.nexters.boolti.data.network.response.GiftResponse
import com.nexters.boolti.data.network.response.ImageResponse
import com.nexters.boolti.domain.request.FreeGiftRequest
import com.nexters.boolti.domain.request.GiftApproveRequest
import javax.inject.Inject

internal class GiftDataSource @Inject constructor(
    private val service: GiftService
) {
    suspend fun receiveGift(request: GiftReceiveRequest): Boolean = service.receiveGift(request)

    suspend fun approveGiftPayment(request: GiftApproveRequest): ApproveGiftPaymentResponse =
        service.approveGiftPayment(request)

    suspend fun createFreeGift(request: FreeGiftRequest): ApproveGiftPaymentResponse =
        service.createFreeGift(request)

    suspend fun getGift(giftUuid: String): GiftResponse = service.getGift(giftUuid)

    suspend fun getGiftImages(): List<ImageResponse> = service.getGiftImages()
}