package com.nexters.boolti.domain.model

data class PreQuestion(
    val id: Long,
    val question: String,
    val description: String,
    val isRequired: Boolean,
)
