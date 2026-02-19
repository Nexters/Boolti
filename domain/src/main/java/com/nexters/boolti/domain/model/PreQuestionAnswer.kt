package com.nexters.boolti.domain.model

data class PreQuestionAnswer(
    val preQuestionId: Long,
    val question: String,
    val description: String,
    val isRequired: Boolean,
    val answer: String,
)
