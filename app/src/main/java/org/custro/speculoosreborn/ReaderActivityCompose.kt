package org.custro.speculoosreborn

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File

class ReaderActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()

        val pageModel: PageModel by viewModels()

        pageModel.onSizeChange(getMetrics())
        pageModel.onFileChange(File("/storage/emulated/0/aoe.cbz"))

        setContent {
            ReaderScreen(pageModel)
        }
    }

    @Composable
    fun ReaderScreen(pageModel: PageModel) {
        val index by pageModel.index.observeAsState(0)
        val maxIndex by pageModel.maxIndex.observeAsState(0)
        val image by pageModel.image.observeAsState(pageModel.emptyBitmap)
        val background = remember {
            val bitmap = BitmapFactory.decodeStream(assets.open("background.png"))
            bitmap.asImageBitmap()
        }
        Background(bitmap = background)
        Page(bitmap = image.asImageBitmap())
        TapBox(index = index, maxIndex = maxIndex, onIndexChange = { pageModel.onIndexChange(it) })
        Text(text = index.toString())
    }

    @Composable
    fun Background(bitmap: ImageBitmap) {
        val (w, h) = remember { getMetrics() }
        TiledImage(
            bitmap = bitmap, width = w, height = h, contentDescription = "background"
        )
    }

    @Composable
    fun Page(bitmap: ImageBitmap) {
        Image(
            bitmap = bitmap,
            contentDescription = "page",
            modifier = Modifier.fillMaxSize()
        )
    }

    @Composable
    fun TapBox(index: Int, maxIndex: Int, onIndexChange: (Int) -> Unit) {
        val (w, h) = remember { getMetrics() }
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(index) {
                detectTapGestures { offset ->  //onTap
                    Log.d("TapBox", "Touched, $index")
                    if (offset.x > 2 * w / 3 /*&& index < maxIndex - 1*/) {
                        onIndexChange(index + 1)
                    } else if (offset.x < w / 3 && maxIndex > 0) {
                        onIndexChange(index - 1)
                    }
                }
            })
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