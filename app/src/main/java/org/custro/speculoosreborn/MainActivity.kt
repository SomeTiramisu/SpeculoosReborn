package org.custro.speculoosreborn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import kotlinx.coroutines.coroutineScope
import org.custro.speculoosreborn.room.AppDatabase
import org.custro.speculoosreborn.room.Manga

class MainActivity : ComponentActivity() {
    private val getArchive =
        registerForActivityResult(object : ActivityResultContracts.OpenDocument() {
            override fun createIntent(context: Context, input: Array<out String>): Intent {
                val intent = super.createIntent(context, input)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                return intent
            }
        }) { uri: Uri? ->
            if (uri != null) {
                val mainModel: MainModel by viewModels()
                mainModel.onArchiveUriChange(uri)
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        val mainModel: MainModel by viewModels()
        setContent {
            Buttons(mainModel)
        }
    }

    @Composable
    fun Buttons(mainModel: MainModel) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextButton(onClick = { pickFile(mainModel) }) {
                Text(text = "Pick")
            }
            TextButton(onClick = { startReader(mainModel) }) {
                Text(text = "Start")
            }
        }
    }
/*
    @Composable
    fun ReadNow(open: Boolean) {
        AlertDialog(onDismissRequest = {},
            title = {
                Text(text = "Read now ?")
            },
            buttons = {
                Row {
                    TextButton(modifier = Modifier.fillMaxWidth(),
                        onClick = {}) {
                        Text("Later")
                    }
                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            startReader()
                        }) {
                        Text(text = "Now")
                    }
                }
            }
        )
    }
*/
    private fun startReader(mainModel: MainModel) {
        val intent = Intent(this, ReaderActivity::class.java)
        intent.data = mainModel.archiveUri.value
        startActivity(intent)
    }

    private fun pickFile(mainModel: MainModel) {
        getArchive.launch(arrayOf("*/*"))
    }
}