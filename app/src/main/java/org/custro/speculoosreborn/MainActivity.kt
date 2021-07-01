package org.custro.speculoosreborn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import java.io.File

class MainActivity : AppCompatActivity() {
    private var archiveFile: File? = File("/storage/emulated/0/aoe.cbz")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.loadLibrary("opencv_java4")

        //startReader(null)
    }

    fun startReader(view: View?) {
        val b = Bundle()
        b.putString("file", archiveFile?.absolutePath)
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }

    fun pickFile(view: View?) {
        //resultLauncher.launch(arrayOf("*/*"))
        MaterialDialog(this).show {
            fileChooser(context, initialDirectory = getExternalStorageDirectory(), filter =  { file -> file.isDirectory || file.extension == "cbz" || file.extension == "cbr" }) { dialog, file ->
                archiveFile = file
            }
        }
    }
}