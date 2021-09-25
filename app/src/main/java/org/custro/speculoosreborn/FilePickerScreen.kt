package org.custro.speculoosreborn

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import java.io.File

@Composable
fun FilePickerScreen(model: FilePickerModel = FilePickerModel()) {
    val currentDir: File by model.currentDir.observeAsState(model.initialDir)
    FileList(files = currentDir.listFiles()?.toList() ?: listOf(),
        onSelectFile = { model.onCurrentDirChange(it) },
        onSelectParent = { model.onParentDir() }
    )
}

@Composable
fun FileList(files: List<File>,
             onSelectFile: (File) -> Unit,
             onSelectParent: () -> Unit
) {
    LazyColumn() {
        item {
            TextButton(onClick = onSelectParent) {
            Text(text = "..")
        } }
        items(files) { file ->
            TextButton(onClick = { onSelectFile(file) }) {
                Text(text = file.name)
            }
        }
    }
}