package org.custro.speculoosreborn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
               alpha: Float = DefaultAlpha,
               colorFilter: ColorFilter? = null) {
    val nwidth = width/bitmap.width+2
    val nheight = height/bitmap.height+2
    repeat (nwidth) {
        Column {
            repeat (nwidth) {
                Row {
                    repeat(nheight) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = contentDescription,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.None,
                            alpha = alpha,
                            colorFilter = colorFilter
                        )
                    }
                }
            }
        }
    }
}