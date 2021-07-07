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

class MainActivity : ComponentActivity() {
    private var archiveUri: Uri? = null
    private val getArchive =
        registerForActivityResult(object : ActivityResultContracts.OpenDocument() {
            override fun createIntent(context: Context, input: Array<out String>): Intent {
                val intent = super.createIntent(context, input)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                return intent
            }
        }) { uri: Uri? ->
            if (uri != null) {
                archiveUri = uri
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            Log.d("MainActivity", "$uri")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        val mainModel: MainModel by viewModels()
        setContent {
            Buttons()
        }
    }

    @Composable
    fun Buttons() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextButton(onClick = { pickFile() }) {
                Text(text = "Pick")
            }
            TextButton(onClick = { startReader() }) {
                Text(text = "Start")
            }
        }
    }

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

    private fun startReader() {
        val intent = Intent(this, ReaderActivity::class.java)
        intent.data = archiveUri
        startActivity(intent)
    }

    private fun pickFile() {
        getArchive.launch(arrayOf("*/*"))
    }
}