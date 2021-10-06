package org.custro.speculoosreborn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@ExperimentalMaterialApi
@Composable
fun FilePickerScreen(model: FilePickerModel = viewModel()) {
    val currentDir: File by model.currentDir.observeAsState(model.initialDir)
    val currentExternalDirName: String by model.currentExternalDirName.observeAsState("")
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { model.onExternalDirChange() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = currentExternalDirName)
        }
        FileList(files = currentDir.listFiles()?.toList() ?: listOf(),
            onSelectFile = { model.onCurrentDirChange(it) },
            onSelectParent = { model.onParentDir() }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun FileList(files: List<File>,
             onSelectFile: (File) -> Unit,
             onSelectParent: () -> Unit
) {
    LazyColumn() {
        item {
            DirListItem(name = "..", onSelectDir = { onSelectParent() })
             }
        items(files) { file ->
            if (file.isFile) {
                FileListItem(name = file.name, onSelectFile = { onSelectFile(file) }, false)
            }
            if (file.isDirectory) {
                DirListItem(name = file.name, onSelectDir = {onSelectFile(file)})
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun FileListItem(
    name: String,
    onSelectFile: () -> Unit,
    checked: Boolean
) {
    ListItem(
        modifier = Modifier.clickable { onSelectFile() },
        text =  {
            Text(text = name)
        },
        //trailing = { Checkbox(checked = checked, onCheckedChange = {})},
        icon =  { Icon(Icons.Filled.Book, null)}
    )
}

@ExperimentalMaterialApi
@Composable
fun DirListItem(
    name: String,
    onSelectDir: () -> Unit,
) {
        ListItem(
            modifier = Modifier.clickable { onSelectDir() },
            text = {
                Text(text = name)
            },
            icon = { Icon(Icons.Filled.FolderOpen, null) }
        )

}


