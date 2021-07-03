package org.custro.speculoosreborn

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File

class ReaderActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()
        setContent {
            ReaderView()
        }
        mTiramisu.init(imageCallback, sizeCallback)
        val req = PageRequest(0, 100, 100, File("/storage/emulated/0/aoe.cbz"))
        mTiramisu.get(req)
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

    @Composable
    fun ReaderView() {
        val (w, h) = getMetrics()
        val background = remember {
            val bitmap = BitmapFactory.decodeStream(assets.open("background.png"))
            bitmap.asImageBitmap()
        }
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "page"
        )
        TiledImage(
            bitmap = background, width = w, height = h, contentDescription = "background"
        )
    }

    private fun getMetrics(): Pair<Int, Int> {
        val metrics: DisplayMetrics = DisplayMetrics()
        //windowManager.defaultDisplay.getRealMetrics(metrics)
        //peekAvailableContext()!!.display!!.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return Pair(100, 100)
    }

    private var image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private val imageCallback = { res: Mat ->
        val bitmap = Bitmap.createBitmap(res.cols(), res.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(res, bitmap)
        image = bitmap
        rememb
    }
    private var size: Int = 0
    private val sizeCallback = { res: Int ->
        size = res
    }

    companion object {
        private val mTiramisu = Tiramisuk()
    }
}