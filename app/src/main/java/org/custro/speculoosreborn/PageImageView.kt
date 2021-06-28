package org.custro.speculoosreborn

import android.content.Context
import android.graphics.Bitmap
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
    private var mImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    //private var mTmpImage
    //private var mResizeTimer


    constructor(context: Context) : super(context) {
        mTiramisu.connectImage { img: Mat ->
            Utils.matToBitmap(img, mImage)
        }
        mTiramisu.connectBookSize { bookSize: Int ->
            mBookSize = bookSize
        }
    }

    fun setIndex(index: Int) {
        mIndex = index
        mReq = PageRequest(index, width, height, File("/storage/emulated/0/aoe.cbz"))
        mTiramisu.get(mReq)
    }
}