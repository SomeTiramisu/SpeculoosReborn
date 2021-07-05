package org.custro.speculoosreborn

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import java.io.File

class MainActivity : ComponentActivity() {
    private var archiveFile: File? = File("/storage/emulated/0/aoe.cbz")
    private var archiveUri: Uri? = null
    val getArchive = registerForActivityResult(object : ActivityResultContracts.OpenDocument() {
        override fun createIntent(context: Context, input: Array<out String>): Intent {
            val intent = super.createIntent(context, input)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            return intent
        }
    }) { uri: Uri? ->
        archiveUri = uri
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

    override fun onStart() {
        super.onStart()
        /*if (applicationContext.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            || applicationContext.checkSelfPermission(MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE), 0)
        }*/
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
        val b = Bundle()
        b.putString("file", archiveFile?.absolutePath)
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }

    private fun pickFile() {
        getArchive.launch(arrayOf("*/*"))
    }
}