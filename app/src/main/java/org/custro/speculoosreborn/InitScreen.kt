package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Transformations
import org.custro.speculoosreborn.room.Manga

@Composable
fun InitScreen(
    initModel: InitModel,
    findManga: () -> Unit,
    setManga: (Uri) -> Unit,
    navigateToReaderScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit
) {
    val mangas: List<Manga> by initModel.getMangas().observeAsState(listOf())
    val mangaCardModels: List<MangaCardModel> by Transformations.map(initModel.getMangas()) {
        it.map { manga ->
            val model = MangaCardModel()
            model.onMangaChange(manga)
            model
        }
    }.observeAsState(initial = listOf())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { findManga() }) {
                Icon(Icons.Filled.Add, contentDescription = "Pick file and add it to library")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navigateToSettingsScreen() }) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) {
        MangaList(cardModels = mangaCardModels,
            onReadManga = { setManga(Uri.parse(it)); navigateToReaderScreen() },
            onDeleteManga = { initModel.deleteManga(it) }
        )
    }
}

@Composable
fun MangaList(
    cardModels: List<MangaCardModel>,
    onReadManga: (String) -> Unit,
    onDeleteManga: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(cardModels) { cardModel ->
            MangaCard(
                model = cardModel,
                onRead = onReadManga,
                onDelete = onDeleteManga
            )
        }
    }
}
