package org.custro.speculoosreborn

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

class ReaderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()

        val pageModel: PageModel by viewModels()
        //val pageModel = ViewModelProvider(this).get(PageModel::class.java)

        pageModel.onSizeChange(getMetrics())
        pageModel.onFileChange(File("/storage/emulated/0/aoe.cbz"))

        setContent {
            ReaderScreen(pageModel)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    @Composable
    fun ReaderScreen(pageModel: PageModel) {
        val index: Int by pageModel.index.observeAsState(0)
        val maxIndex: Int by pageModel.maxIndex.observeAsState(0)
        val image: ImageBitmap by pageModel.image.observeAsState(ImageBitmap(1, 1))
        val hiddenSlider: Boolean by pageModel.hiddenSlider.observeAsState(false)
        val background = remember {
            val bitmap = BitmapFactory.decodeStream(assets.open("background.png"))
            bitmap.asImageBitmap()
        }
        Background(bitmap = background)
        Page(bitmap = image)
        TapBox(
            index = index,
            maxIndex = maxIndex,
            onIndexChange = { pageModel.onIndexChange(it) },
            hiddenSlider = hiddenSlider,
            onHiddenSliderChange = { pageModel.onHiddenSliderChange(it) }
        )
        SliderView(
            index = index,
            maxIndex = maxIndex,
            onIndexChange = { pageModel.onIndexChange(it) },
            hidden = hiddenSlider
        )
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
    fun TapBox(
        index: Int,
        maxIndex: Int,
        onIndexChange: (Int) -> Unit,
        hiddenSlider: Boolean,
        onHiddenSliderChange: (Boolean) -> Unit
    ) {
        val (w, _) = remember { getMetrics() }
        val key = index+maxIndex* (if (hiddenSlider) 0 else 1)
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(key) {
                detectTapGestures { offset ->  //onTap
                    Log.d("TapBox", "Touched, $index, $maxIndex")
                    if (offset.x > 2 * w / 3 && index < maxIndex - 1) {
                        onIndexChange(index + 1)
                    } else if (offset.x < w / 3 && index > 0) {
                        onIndexChange(index - 1)
                    }
                    if (!hiddenSlider && index >= 0 && index <= maxIndex - 1) {
                        onHiddenSliderChange(true)
                    } else if (offset.x > w / 3 && offset.x < 2 * w / 3) {
                        onHiddenSliderChange(false)
                    }
                }
            })
    }

    @Composable
    fun IndexSlider(index: Int, maxIndex: Int, onIndexChange: (Int) -> Unit) {
        var sliderPosition by remember { mutableStateOf(0.0f) }
        var sliding by remember { mutableStateOf(false) }
        Slider(value = if (sliding) sliderPosition else index.toFloat(),
            steps = 0,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            onValueChange = { sliding = true; sliderPosition = it },
            valueRange = if (maxIndex > 0) 0.0f..(maxIndex - 1).toFloat() else 0.0f..1.0f,
            onValueChangeFinished = {
                sliding = false; onIndexChange(sliderPosition.toInt())
                Log.d("IndexSlider", "changed: $index, $maxIndex")
            }
        )
    }

    @Composable
    fun SliderView(
        index: Int,
        maxIndex: Int,
        onIndexChange: (Int) -> Unit,
        hidden: Boolean = false
    ) {
        if (hidden) return
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = index.toString()
            )
            IndexSlider(
                index = index,
                maxIndex = maxIndex,
                onIndexChange = onIndexChange
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun getMetrics(): Pair<Int, Int> {
        val metrics = DisplayMetrics()
        //windowManager.defaultDisplay.getRealMetrics(metrics)
        //peekAvailableContext()!!.display!!.getRealMetrics(metrics)
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return Pair(width, height)
    }

    @Suppress("DEPRECATION")
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