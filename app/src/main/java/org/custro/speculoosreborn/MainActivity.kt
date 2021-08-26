package org.custro.speculoosreborn

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache

class MainActivity : ComponentActivity() {
    private val getArchive =
        registerForActivityResult(object : ActivityResultContracts.OpenDocument() {
            override fun createIntent(context: Context, input: Array<out String>): Intent {
                val intent = super.createIntent(context, input)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                return intent
            }
        }) { uri: Uri? ->
            if (uri != null) {
                val mainModel: MainModel by viewModels()
                mainModel.onArchiveUriChange(uri)
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

            }
        }

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        val mainModel: MainModel by viewModels()
        val pageModel: PageModel by viewModels()
        pageModel.onSizeChange(getMetrics())
        setContent {
            MainNavigation(mainModel, pageModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PageCache.delete()
    }

    @ExperimentalAnimationApi
    @Composable
    fun MainNavigation(mainModel: MainModel, pageModel: PageModel) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "initScreen") {
            composable("initScreen") {
                showSystemUi()
                InitScreen(mainModel, navController)
            }
            composable("readerScreen") {
                hideSystemUi()
                mainModel.archiveUri.value?.let { uri -> pageModel.onUriChange(uri) }
                ReaderScreen(pageModel)
            }
        }
    }

    @Composable
    fun InitScreen(mainModel: MainModel, navController: NavController) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextButton(onClick = { getArchive.launch(arrayOf("*/*")) }) {
                Text(text = "Pick")
            }
            TextButton(onClick = {
                navController.navigate("readerScreen")
                }) {
                Text(text = "Start")
            }
        }
    }

    @ExperimentalAnimationApi
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
        val key = index + maxIndex * (if (hiddenSlider) 0 else 1)
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
                sliding = false
                onIndexChange(sliderPosition.toInt())
                Log.d("IndexSlider", "changed: $index, $maxIndex")
            }
        )
    }

    @ExperimentalAnimationApi
    @Composable
    fun SliderView(
        index: Int,
        maxIndex: Int,
        onIndexChange: (Int) -> Unit,
        hidden: Boolean = false
    ) {
        AnimatedVisibility(
            visible = !hidden,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = index.toString()
                )
                IndexSlider(
                    index = index,
                    maxIndex = maxIndex,
                    onIndexChange = onIndexChange
                )
            }
        }
    }

    @Composable
    fun TiledImage(
        bitmap: ImageBitmap,
        width: Int,
        height: Int,
        contentDescription: String?,
        alpha: Float = DefaultAlpha,
        colorFilter: ColorFilter? = null
    ) {
        val nwidth = width / bitmap.width + 2
        val nheight = height / bitmap.height + 2
        repeat(nwidth) {
            Column {
                repeat(nwidth) {
                    Row {
                        repeat(nheight) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = contentDescription,
                                alignment = Alignment.Center,
                                contentScale = ContentScale.None,
                                alpha = alpha,
                                colorFilter = colorFilter
                            )
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getMetrics(): Pair<Int, Int> {
        val metrics = DisplayMetrics()
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

    @Suppress("DEPRECATION")
    private fun hideSystemUiNew() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    @Suppress("DEPRECATION")
    private fun showSystemUi() {
        window.decorView.systemUiVisibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    private fun showSystemUiNew() {
        window.decorView.systemUiVisibility = View.VISIBLE
    }
}