package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.GiftService
import com.nexters.boolti.data.network.request.GiftCancelRequest
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.ApproveGiftPaymentResponse
import com.nexters.boolti.data.network.response.GiftImageResponse
import com.nexters.boolti.data.network.response.GiftPaymentInfoResponse
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

    suspend fun getGiftImages(): List<GiftImageResponse> = service.getGiftImages()

    suspend fun getGiftPaymentInfo(giftId: String): GiftPaymentInfoResponse =
        service.getGiftPaymentInfo(giftId)

    suspend fun cancelGift(giftUuid: String): Boolean =
        service.cancelGift(GiftCancelRequest(giftUuid = giftUuid))
}