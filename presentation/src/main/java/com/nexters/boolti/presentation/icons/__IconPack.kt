package com.nexters.boolti.presentation.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.nexters.boolti.presentation.icons.iconpack.Kakaotalk
import kotlin.collections.List as ____KtList

public object IconPack

private var __icons: ____KtList<ImageVector>? = null

public val IconPack.icons: ____KtList<ImageVector>
  get() {
    if (__icons != null) {
      return __icons!!
    }
    __icons= listOf(Kakaotalk)
    return __icons!!
  }
