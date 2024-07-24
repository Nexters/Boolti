package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.ApproveGiftPaymentResponse
import com.nexters.boolti.data.network.response.GiftPaymentInfoResponse
import com.nexters.boolti.data.network.response.GiftResponse
import com.nexters.boolti.data.network.response.ImageResponse
import com.nexters.boolti.domain.request.GiftApproveRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface GiftService {
    @POST("/app/api/v1/order/receive-gift")
    suspend fun receiveGift(@Body request: GiftReceiveRequest): Boolean

    @POST("/app/api/v1/order/gift-approve-payment")
    suspend fun approveGiftPayment(@Body request: GiftApproveRequest): ApproveGiftPaymentResponse

    @GET("/app/api/v1/gift/{giftUuid}")
    suspend fun getGift(@Path("giftUuid") giftUuid: String): GiftResponse

    @GET("/app/api/v1/gift/img-list")
    suspend fun getGiftImages(): List<ImageResponse>

    @GET("app/api/v1/gift/approve-payment/{giftId}")
    suspend fun getGiftPaymentInfo(): GiftPaymentInfoResponse
}