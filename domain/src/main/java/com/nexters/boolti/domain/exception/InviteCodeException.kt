package com.nexters.boolti.domain.exception

import com.nexters.boolti.domain.model.InviteCodeStatus

data class InviteCodeException(
    val status: InviteCodeStatus? = null,
) : Exception(status?.toString())
