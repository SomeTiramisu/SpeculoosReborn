package org.custro.speculoosreborn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory

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
        ParserFactory.resolver = contentResolver
        System.loadLibrary("opencv_java4")
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

    private fun startReader() {
        val intent = Intent(this, ReaderActivity::class.java)
        intent.data = archiveUri
        startActivity(intent)
    }

    private fun pickFile() {
        getArchive.launch(arrayOf("*/*"))
    }
}