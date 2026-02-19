package com.nexters.boolti.domain.request

data class SubmitPreQuestionAnswersRequest(
    val reservationId: String,
    val answers: List<PreQuestionAnswerRequest>,
)

data class PreQuestionAnswerRequest(
    val preQuestionId: Long,
    val answer: String,
)
