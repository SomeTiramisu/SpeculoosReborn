package org.custro.speculoosreborn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun InitScreen(findManga: () -> Unit, navigateToReaderScreen: () -> Unit) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { findManga() /*/* getArchive.launch(arrayOf("*/*")) */ }) {
            Icon(Icons.Filled.Add, contentDescription = "Pick file and add it to library")
        }
    }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextButton(onClick = {
                navigateToReaderScreen()
                //navController.navigate("readerScreen")
            }) {
                Text(text = "Start")
            }
        }
    }
}