package com.nexters.boolti.presentation.icons.iconpack

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.icons.IconPack

public val IconPack.Kakaotalk: ImageVector
    get() {
        if (_kakaotalk != null) {
            return _kakaotalk!!
        }
        _kakaotalk = Builder(name = "Kakaotalk", defaultWidth = 20.0.dp, defaultHeight = 21.0.dp,
                viewportWidth = 20.0f, viewportHeight = 21.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(10.0473f, 2.657f)
                curveTo(5.4236f, 2.657f, 1.6667f, 5.6962f, 1.6667f, 9.4217f)
                curveTo(1.6667f, 11.7746f, 3.208f, 13.8335f, 5.4236f, 15.108f)
                lineTo(4.8456f, 18.3433f)
                lineTo(8.4097f, 15.9903f)
                curveTo(8.8914f, 16.0884f, 9.4694f, 16.0884f, 9.951f, 16.0884f)
                curveTo(14.5748f, 16.0884f, 18.3316f, 13.0491f, 18.3316f, 9.3237f)
                curveTo(18.4279f, 5.6962f, 14.6711f, 2.657f, 10.0473f, 2.657f)
                close()
            }
        }
        .build()
        return _kakaotalk!!
    }

private var _kakaotalk: ImageVector? = null

@Composable
@Preview
fun KakaotalkPreview() {
    Icon(imageVector = IconPack.Kakaotalk, contentDescription = "")
}