package org.custro.speculoosreborn

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.loadLibrary("opencv_java4")

        //startReader(null)
    }

    fun startReader(view: View?) {
        val b = Bundle()
        b.putString("file", "/storage/emulated/0/aoe.cbz")
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }
}