package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@ExperimentalFoundationApi
@Composable
fun MangaCard(model: MangaCardModel = viewModel(), onRead: (uri: String) -> Unit, onDelete: (uri: String) -> Unit) {
    var isMenuExpanded: Boolean by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = { onRead(model.localUri) },
                onLongClick = { isMenuExpanded = true })
            .height(100.dp)
    ) {
        val cover: ImageBitmap by model.cover.observeAsState(ImageBitmap(1, 1))
        Row {
            Image(bitmap = cover, contentDescription = "front page")
            Column(verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()) {
                Uri.parse(model.uri).lastPathSegment?.let { it1 ->
                    Text(
                        text = it1.split(':').last().split('/').last(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
        DropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }) {
            DropdownMenuItem(onClick = { onDelete(model.uri) }) {
                Text(text = "Remove", color = Color.Red)
            }
        }
    }
}