package com.nexters.boolti.presentation.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.nexters.boolti.presentation.icons.iconpack.ArrowBack
import kotlin.collections.List as ____KtList

public object IconPack

private var __Icons: ____KtList<ImageVector>? = null

public val IconPack.Icons: ____KtList<ImageVector>
  get() {
    if (__Icons != null) {
      return __Icons!!
    }
    __Icons= listOf(ArrowBack)
    return __Icons!!
  }
