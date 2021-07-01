package org.custro.speculoosreborn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.afollestad.materialdialogs.files.FileFilter
import java.io.File

class MainActivity : AppCompatActivity() {
    private val resultLauncher = registerForActivityResult(OpenDocument()) { uri: Uri ->
        path = "storage/emulated/0/" + uri.path?.split(":")?.last()
        Log.d("MainActivity", "$path")
    }
    private var path: String? = "/storage/emulated/0/aoe.cbz"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.loadLibrary("opencv_java4")

        //startReader(null)
    }

    fun startReader(view: View?) {
        val b = Bundle()
        b.putString("file", path)
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }

    fun pickFile(view: View?) {
        //resultLauncher.launch(arrayOf("*/*"))
        MaterialDialog(this).show {
            fileChooser(context, initialDirectory = getExternalStorageDirectory(), filter =  { file -> file.isDirectory || file.extension == "cbz" }) { dialog, file ->
                path = file.absolutePath
            }
        }
        //resultLauncher.launch(arrayOf("application/cbz"))
    }
}