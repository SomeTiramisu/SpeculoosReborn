package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MangaCard(model: MangaCardModel = MangaCardModel(), onRead: (uri: String) -> Unit, onDelete: (uri: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val cover: ImageBitmap by model.cover.observeAsState(ImageBitmap(1, 1))
        Row {
            Image(bitmap = cover, contentDescription = "front page")
            Column(verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)) {
                Uri.parse(model.uri).lastPathSegment?.let { it1 ->
                    Text(
                        text = it1.split(':').last().split('/').last(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                Row {
                    TextButton(onClick = { onDelete(model.uri) },
                    modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(text = "Remove")
                        Icon(Icons.Filled.RemoveCircleOutline , "delete manga from list")
                    }
                    TextButton(onClick = { onRead(model.localUri) },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(text = "Read")
                        Icon(Icons.Filled.PlayArrow, "read manga" )
                    }
                }
            }
        }
    }
}