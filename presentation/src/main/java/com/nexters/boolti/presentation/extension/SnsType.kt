package com.nexters.boolti.presentation.extension

import androidx.annotation.DrawableRes
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.Sns.SnsType.INSTAGRAM
import com.nexters.boolti.domain.model.Sns.SnsType.YOUTUBE
import com.nexters.boolti.presentation.R

val Sns.SnsType.label: String
    get() = when (this) {
        INSTAGRAM -> "instagram"
        YOUTUBE -> "youtube"
    }

val Sns.SnsType.icon: Int
    @DrawableRes
    get() = when (this) {
        INSTAGRAM -> R.drawable.ic_logo_instagram
        YOUTUBE -> R.drawable.ic_logo_youtube
    }
