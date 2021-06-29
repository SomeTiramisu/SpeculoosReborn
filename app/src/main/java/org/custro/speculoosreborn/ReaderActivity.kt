package org.custro.speculoosreborn

import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream


class ReaderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        hideSystemUi()
        supportActionBar?.hide()

        val file = intent.extras?.getString("file")
        if (file != null) {
            val page = findViewById<PageImageView>(R.id.pageImageView)
            page.index = 70

        }

        val bgStream = assets.open("background.png")
        setBackground(bgStream)
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

    private fun setBackground(stream: InputStream) {
        val bgBitmap = BitmapFactory.decodeStream(stream)
        val bgDrawable = BitmapDrawable(resources, bgBitmap)
        bgDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        findViewById<ImageView>(R.id.pageImageView).background = bgDrawable
    }
}