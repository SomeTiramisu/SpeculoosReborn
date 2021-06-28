package org.custro.speculoosreborn

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.opencv.android.Utils
import java.io.File


class ReaderActivity : AppCompatActivity() {
    private val tiramisuk = Tiramisuk()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        hideSystemUi()
        supportActionBar?.hide()

        val b = intent.extras
        var file: String? = null
        if (b != null) {
            file = b.getString("file")
        }
        if (file != null) {
            val page = findViewById<PageImageView>(R.id.pageImageView)
            page.setIndex(0)

        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }


    private fun hideSystemUi() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}