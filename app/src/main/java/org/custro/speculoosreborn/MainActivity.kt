package org.custro.speculoosreborn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AppCompatActivity

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
        resultLauncher.launch(arrayOf("*/*"))
    }
}