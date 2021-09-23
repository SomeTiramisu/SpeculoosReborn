package org.custro.speculoosreborn

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
                Log.d("getArchive", "selected: $uri")
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                val savedUri = PageCache.saveData(uri)
                initModel.insertManga(savedUri)
            }
        }

    @Suppress("DEPRECATION")
    private fun parseUri(uri: Uri): Uri {
        val lastSegment = uri.lastPathSegment!!
        if (lastSegment.split(":").first() == "primary") {
            return Uri.parse("${Environment.getExternalStorageDirectory()}/${lastSegment.split(":").last()}")
        }
        return uri
    }


    private val initModel: InitModel by viewModels()
    private val readerModel: ReaderModel by viewModels()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.loadLibrary("opencv_java4")

        showOnCutout()
        setContent {
            MainNavigation(
                readerModel = readerModel,
                initModel = initModel,
                findManga = { getArchive.launch(arrayOf("*/*")) },
                showSystemUi = { showSystemUiNew() },
                hideSystemUi = { hideSystemUiNew() }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        readerModel.onSizeChange(getMetrics())
        readerModel.onBackgroundChange(
            BitmapFactory.decodeStream(assets.open("background.png")).asImageBitmap()
        )
    }

    @Suppress("DEPRECATION")
    private fun getMetrics(): Pair<Int, Int> {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        return Pair(width, height)
    }


    private fun hideSystemUiNew() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showOnCutout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    private fun showSystemUiNew() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }
}