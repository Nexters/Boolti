package com.nexters.boolti.data.util

import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.serializer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// 2024-12-29T00:00:00
internal fun String.toLocalDate(): LocalDate = this.toLocalDateTime().toLocalDate()

internal fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

internal fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this.format(formatter))

internal fun String.toReservationState(): ReservationState {
    return when (this) {
        "WAITING_FOR_DEPOSIT" -> ReservationState.DEPOSITING
        "CANCELLED" -> ReservationState.CANCELED
        "RESERVATION_COMPLETED" -> ReservationState.RESERVED
        "WAITING_FOR_REFUND" -> ReservationState.REFUNDING
        "REFUND_COMPLETED" -> ReservationState.REFUNDED
        "WAITING_FOR_GIFT_RECEIPT" -> ReservationState.REGISTERING_GIFT
        // TODO: 선물 등록 완료 상태 추가
        else -> ReservationState.UNDEFINED
    }
}

internal fun String?.toPaymentType(): PaymentType {
    return when (this) {
        "BANK_TRANSFER", "ACCOUNT_TRANSFER" -> PaymentType.ACCOUNT_TRANSFER
        "CARD" -> PaymentType.CARD
        "SIMPLE_PAYMENT" -> PaymentType.SIMPLE_PAYMENT
        "FREE" -> PaymentType.FREE
        else -> PaymentType.UNDEFINED
    }
}

internal fun File.toImageMultipartBody(): MultipartBody.Part = MultipartBody.Part.createFormData(
    name = "image",
    filename = name,
    body = asRequestBody("image/*".toMediaType())
)

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

internal fun <T> JsonObject.toPagingData(
    itemSerializer: KSerializer<T>,
): PagingData<T> {
    val currentPage = this["currentPage"]?.jsonPrimitive?.int ?: 0
    val pageSize = this["pageSize"]?.jsonPrimitive?.int ?: 0
    val totalElements = this["totalElements"]?.jsonPrimitive?.long ?: 0L
    val totalPages = this["totalPages"]?.jsonPrimitive?.int ?: 0
    val hasNext = this["hasNext"]?.jsonPrimitive?.boolean ?: false

    // 배열 필드 하나 찾기
    val firstArray = entries
        .firstOrNull { (_, v) -> v is JsonArray }
        ?.value as? JsonArray

    val items: List<T> = if (firstArray == null) {
        emptyList()
    } else {
        json.decodeFromJsonElement(ListSerializer(itemSerializer), firstArray)
    }

    return PagingData(
        items = items,
        currentPage = currentPage,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        hasNext = hasNext,
    )
}

internal suspend inline fun <reified T> getPagingData(
    call: suspend () -> JsonObject,
): PagingData<T> = call().toPagingData(serializer<T>())
