package com.nexters.boolti.domain.extension

val Boolean.YN: String
    get() = if (this) "Y" else "N"
