package com.nexters.boolti.domain.model

@JvmInline
value class ToggleResult(val value: Boolean) {
    operator fun invoke() = value
}
