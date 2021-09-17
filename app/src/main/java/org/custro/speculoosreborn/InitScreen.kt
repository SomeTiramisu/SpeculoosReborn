package org.custro.speculoosreborn

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val mangas: List<Manga> by initModel.getMangas().observeAsState(listOf(Manga("")))
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { findManga() }) {
            Icon(Icons.Filled.Add, contentDescription = "Pick file and add it to library")
        }
    }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState(), enabled = true)
        ) {
            for (m in mangas) {
                MangaCard(manga = m,
                    onRead = { setManga(Uri.parse(it)); navigateToReaderScreen() },
                    onDelete = { initModel.deleteManga(it) })
            }
            TextButton(onClick = {
                navigateToReaderScreen()
            }) {
                Text(text = "Start")
            }
        }
    }
}

@Composable
fun MangaCard(manga: Manga, onRead: (uri: String) -> Unit, onDelete: (uri: String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .height(100.dp)
    ) {
        Column( verticalArrangement = Arrangement.Center ) {
            Uri.parse(manga.uri).lastPathSegment?.let { it1 ->
                Text(
                    text = it1.split(':').last().split('/').last()
                )
            }
            Row( horizontalArrangement = Arrangement.End ) {
                Button(onClick = { onDelete(manga.uri) }) {
                    Text(text = "Remove")
                }
                Button(onClick = { onRead(manga.uri) }) {
                    Text(text = "Read")
                }
            }
        }
    }
}