package org.custro.speculoosreborn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File

class PageImageView: androidx.appcompat.widget.AppCompatImageView {
    private val mTiramisu = Tiramisuk()
    private var mBookSize: Int = 0
    private var mPreloaderProgress: Int = 0
    private var mIndex: Int = 0
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

    fun init() {
        mTiramisu.connectImage { img: Mat ->
            mImage = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(img, mImage)
            runBlocking(Dispatchers.Main) {
                setImageBitmap(mImage)
            }
        }
        mTiramisu.connectBookSize { bookSize: Int ->
            mBookSize = bookSize
        }
    }

    fun setIndex(index: Int) {
        mIndex = index
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        Log.d("SIZE", "$width, $height")
        mReq = PageRequest(index, width, height, File("/storage/emulated/0/aoe.cbz"))
        //mReq = PageRequest(index, 100, 100, File("/storage/emulated/0/aoe.cbz"))
        mTiramisu.get(mReq)
    }
}