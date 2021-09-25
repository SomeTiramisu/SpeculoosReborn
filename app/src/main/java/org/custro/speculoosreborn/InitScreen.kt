package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.custro.speculoosreborn.room.Manga

@Composable
fun InitScreen(
    initModel: InitModel,
    findManga: () -> Unit,
    setManga: (Uri) -> Unit,
    navigateToReaderScreen: () -> Unit
) {
    val mangas: List<Manga> by initModel.getMangas().observeAsState(listOf())
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { findManga() }) {
            Icon(Icons.Filled.Add, contentDescription = "Pick file and add it to library")
        }
    }) {
        MangaList(mangas = mangas,
            onReadManga = { setManga(Uri.parse(it)); navigateToReaderScreen() },
            onDeleteManga = { initModel.deleteManga(it) }
        )
    }
}

@Composable
fun MangaList(
    mangas: List<Manga>,
    onReadManga: (String) -> Unit,
    onDeleteManga: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(mangas) { manga ->
            val cardModel = MangaCardModel()
            cardModel.onMangaChange(manga)
            MangaCard(model = cardModel,
                onRead = onReadManga,
                onDelete = onDeleteManga)
        }
    }
}
