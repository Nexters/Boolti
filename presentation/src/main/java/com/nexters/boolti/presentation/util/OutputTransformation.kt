package com.nexters.boolti.presentation.util

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert

class PhoneNumberOutputTransformation(
    private val sep: String = "-",
) : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 3) insert(3, sep)
        if (length > 8) insert(8, sep)
    }
}
