package org.custro.speculoosreborn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun TiledImage(bitmap: ImageBitmap,
               width: Int,
               height: Int,
               contentDescription: String?,
               modifier: Modifier = Modifier,
               alignment: Alignment = Alignment.Center,
               contentScale: ContentScale = ContentScale.None,
               alpha: Float = DefaultAlpha,
               colorFilter: ColorFilter? = null) {
    val nwidth = width/bitmap.width+1
    val nheight = height/bitmap.height+1
    repeat (nwidth) {
        Column {
            repeat (nheight) {
                Image(bitmap = bitmap,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }
        }
    }
}