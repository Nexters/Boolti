package com.nexters.boolti.domain.model

sealed interface InviteCodeStatus {
    data object Default : InviteCodeStatus
    data object Empty : InviteCodeStatus
    data object Invalid : InviteCodeStatus
    data object Duplicated : InviteCodeStatus
    data object Valid : InviteCodeStatus

    companion object {
        fun fromString(type: String?): InviteCodeStatus = when (type?.trim()?.uppercase()) {
            "INVITE_CODE_NOT_FOUND" -> Invalid
            "USED_INVITE_CODE" -> Duplicated
            else -> Default
        }
    }
}
