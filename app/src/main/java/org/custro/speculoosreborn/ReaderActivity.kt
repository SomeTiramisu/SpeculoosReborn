package org.custro.speculoosreborn

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.io.InputStream


class ReaderActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        hideSystemUi()
        //supportActionBar?.hide()

        indexView = findViewById(R.id.indexView)
        indexView?.text = index.toString()
        pageImageView = findViewById(R.id.pageImageView)
        file = File(intent.extras!!.getString("file")!!)
        pageImageView?.setOnTouchListener { v, event -> onPageTouchEvent(v, event) }

        seekBar = findViewById(R.id.seekBar)
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                indexView?.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                index = seekBar.progress
            }
        })

        val bgStream = assets.open("background.png")
        setBackground(bgStream)
        bgStream.close()

        mTiramisu.init({ img: Mat -> setImage(img) }, { value: Int -> bookSize = value })

        index = savedInstanceState?.getInt("index") ?: 0
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("index", index)
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
    }

    private fun setBackground(stream: InputStream) {
        val bgBitmap = BitmapFactory.decodeStream(stream)
        val bgDrawable = BitmapDrawable(resources, bgBitmap)
        bgDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        pageImageView?.background = bgDrawable
    }

    private fun setImage(img: Mat) {
        val image = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(img, image)
        runBlocking(Dispatchers.Main) {
            pageImageView?.setImageBitmap(image) //workaround to call setImageBitmap in main thread
        }
    }

    private fun onPageTouchEvent(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                //Log.d("PageImageView", "Touched")
                if (event.x > 2 * v.width / 3 && index < bookSize - 1) {
                    index += 1
                } else if (event.x < v.width / 3 && index > 0) {
                    index -= 1
                }
                if (seekBar?.visibility == View.VISIBLE && index >= 0 && index <= bookSize-1) {
                    seekBar?.visibility = View.INVISIBLE
                    indexView?.visibility = View.INVISIBLE
                } else if ((event.x > v.width/3 && event.x < 2*v.width/3) /*|| index == 0 || index == bookSize*/) {
                    seekBar?.visibility = View.VISIBLE
                    indexView?.visibility = View.VISIBLE
                }
            }
            else -> {}
        }
        v.performClick()
        return true
    }

    private var pageImageView: ImageView? = null
    private var seekBar: SeekBar? = null
    private var indexView: TextView? = null
    private var file: File? = null
        set(value) {
            field = value
            index = 0
        }
    private var bookSize: Int = 0
        set(value) {
            field = value
            seekBar?.max = value-1
        }
    private var preloaderProgress: Int = 0
    private var index: Int = 0
        set (value) {
            field = value
            seekBar?.progress = value
            indexView?.text = value.toString()
            val metrics: DisplayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(metrics)
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            //val bounds = windowManager.currentWindowMetrics.bounds //api 30
            Log.d("ReaderActivity", "$width, $height}")
            val req = PageRequest(index, width, height, file)
            mTiramisu.get(req)
        }
    companion object {
        private val mTiramisu = Tiramisuk()
    }
}