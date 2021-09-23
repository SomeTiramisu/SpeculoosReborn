package org.custro.speculoosreborn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import java.io.File

@Composable
fun FilePickerScreen(model: FilePickerModel = FilePickerModel()) {
    val currentDir: File by model.currentDir.observeAsState(model.initialDir)
    Column(            modifier = Modifier
        .fillMaxSize()
        .verticalScroll(state = rememberScrollState(), enabled = true)
    ) {
        TextButton(onClick = { model.onParentDir() }) {
            Text(text = "..")
        }
        for (x in currentDir.listFiles() ?: Array<File>(0) { File("") }) {
            TextButton(onClick = { model.onCurrentDirChange(x) }) {
                Text(text = x.name)
            }
        }
    }
}