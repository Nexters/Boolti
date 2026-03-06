package com.nexters.boolti.presentation.util

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class PhoneNumberOutputTransformation(
    private val sep: Char = '-',
) : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 3) replace(3, 3, sep.toString())
        if (length > 8) replace(8, 8, sep.toString())
    }
}
