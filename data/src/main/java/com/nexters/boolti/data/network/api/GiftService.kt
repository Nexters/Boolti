package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.request.GiftCancelRequest
import com.nexters.boolti.data.network.request.GiftReceiveRequest
import com.nexters.boolti.data.network.response.ApproveGiftPaymentResponse
import com.nexters.boolti.data.network.response.GiftImageResponse
import com.nexters.boolti.data.network.response.GiftPaymentInfoResponse
import com.nexters.boolti.data.network.response.GiftResponse
import com.nexters.boolti.data.network.response.ImageResponse
import com.nexters.boolti.domain.request.FreeGiftRequest
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

    @POST("/app/api/v1/order/free-gift-reservation")
    suspend fun createFreeGift(@Body request: FreeGiftRequest): ApproveGiftPaymentResponse

    @GET("/app/api/v1/gift/{giftUuid}")
    suspend fun getGift(@Path("giftUuid") giftUuid: String): GiftResponse

    @GET("/app/api/v1/gift/img-list")
    suspend fun getGiftImages(): List<GiftImageResponse>

    @GET("app/api/v1/gift/approve-payment-info/{giftId}")
    suspend fun getGiftPaymentInfo(@Path("giftId") giftId: String): GiftPaymentInfoResponse

    @POST("app/api/v1/order/cancel-gift")
    suspend fun cancelGift(@Body request: GiftCancelRequest): Boolean

    @POST("app/api/v1/order/cancel-receive-gift")
    suspend fun cancelReceiveGift(@Body request: GiftCancelRequest): Boolean
}