package org.custro.speculoosreborn

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
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
        //val (w, h) = getMetrics()
        //val req = PageRequest(0, w, h, File("/storage/emulated/0/aoe.cbz"))
        //mTiramisu.get(req)
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
        val (w, h) = remember { getMetrics() }
        val background = remember {
            val bitmap = BitmapFactory.decodeStream(assets.open("background.png"))
            bitmap.asImageBitmap()
        }
        val image = remember {
            mutableStateOf(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888))
        }
        val imageCallback = remember {
            { res: Mat ->
                Log.d("ReaderActivityCompose", "${res.cols()}, ${res.rows()}")
                val bitmap = Bitmap.createBitmap(res.cols(), res.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(res, bitmap)
                image.value = bitmap
            }
        }
        val firstImage = remember {
            mTiramisu.connectImageCallback(imageCallback)
            val req = PageRequest(0, w, h, File("/storage/emulated/0/aoe.cbz"))
            mTiramisu.get(req)
            true
        }
        TiledImage(
            bitmap = background, width = w, height = h, contentDescription = "background"
        )
        Image(
            bitmap = image.value.asImageBitmap(),
            contentDescription = "page"
        )
    }

    private fun getMetrics(): Pair<Int, Int> {
        val metrics: DisplayMetrics = DisplayMetrics()
        //windowManager.defaultDisplay.getRealMetrics(metrics)
        //peekAvailableContext()!!.display!!.getRealMetrics(metrics)
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return Pair(width, height)
    }

    companion object {
        private val mTiramisu = Tiramisuk()
    }
}