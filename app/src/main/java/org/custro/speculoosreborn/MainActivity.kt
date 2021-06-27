package org.custro.speculoosreborn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun startReader(view: View) {
        val b = Bundle()
        b.putString("file", "/storage/emulated/0/aoe.cbz")
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
    }
}