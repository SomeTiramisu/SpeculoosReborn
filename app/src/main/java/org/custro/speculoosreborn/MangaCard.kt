package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.custro.speculoosreborn.room.Manga

@Composable
fun MangaCard(model: MangaCardModel, onRead: (uri: String) -> Unit, onDelete: (uri: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val cover: ImageBitmap by model.cover.observeAsState(ImageBitmap(1, 1))
        Row() {
            Image(bitmap = cover, contentDescription = "front page")
            Column(verticalArrangement = Arrangement.Center) {
                Uri.parse(model.uri).lastPathSegment?.let { it1 ->
                    Text(
                        text = it1.split(':').last().split('/').last()
                    )
                }
                Row(horizontalArrangement = Arrangement.End) {
                    Button(onClick = { onDelete(model.uri) }) {
                        Text(text = "Remove")
                    }
                    Button(onClick = { onRead(model.localUri) }) {
                        Text(text = "Read")
                    }
                }
            }
        }
    }
}