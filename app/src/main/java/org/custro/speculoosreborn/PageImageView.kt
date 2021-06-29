package org.custro.speculoosreborn

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File

class PageImageView: androidx.appcompat.widget.AppCompatImageView {
    private val mTiramisu = Tiramisuk()
    var file: File? = null
        set(value) {
            field = value
            index = 0
        }
    var bookSize: Int = 0
        private set
    var preloaderProgress: Int = 0
        private set
    var index: Int = 0
        set(value) {
            field = value
            val metrics: DisplayMetrics = context.resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            Log.d("SIZE", "$width, $height")
            mReq = PageRequest(index, width, height, file)
            //mReq = PageRequest(index, 100, 100, File("/storage/emulated/0/aoe.cbz"))
            mTiramisu.get(mReq)
        }
    private var mReq = PageRequest()
    private var mImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    //private var mTmpImage
    //private var mResizeTimer


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    private fun init() {
        mTiramisu.connectImage { img: Mat ->
            mImage = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(img, mImage)
            runBlocking(Dispatchers.Main) {
                setImageBitmap(mImage) //workaround to call setImageBitmap in main thread
            }
        }
        mTiramisu.connectBookSize { value: Int ->
            bookSize = value
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("PageImageView", "Touched")
        if (event!!.x > 2*width/3 && index < bookSize-1) {
            index++
        } else if (event!!.x < width/3 && index > 0) {
            index--
        }
        return true
        return super.onTouchEvent(event)
    }
}