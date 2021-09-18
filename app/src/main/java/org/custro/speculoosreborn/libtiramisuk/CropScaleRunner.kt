package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.*
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Mat
import org.opencv.core.Rect

class CropScaleRunner(private val width: Int, private val height: Int, private val getPage: () -> ByteArray, private val doDetect: Boolean = true, private val doScale: Boolean = true) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var mPageResJob: Deferred<Mat>? = null

    fun preload() {
        if (mPageResJob == null) {
            mPageResJob = scope.async(start = CoroutineStart.LAZY) {
                val pngRes: PngPair
                withContext(Dispatchers.IO) {pngRes = cropDetect()}
                cropScale(pngRes)
            }
            mPageResJob!!.start()
        }
    }

    suspend fun get(): Mat {
        if (mPageResJob == null) {
            clear()
            preload()
        }
        return mPageResJob!!.await()
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
    }

    private fun cropScale(p: PngPair): Mat {
        val img = fromByteArray(p.img)
        if (!img.empty() && doScale) {
            cropScaleProcess(img, img, p.rec, width, height)
            if (p.isBlack) {
                //Log.d("Runner", "is black")
                addBlackBorders(img, img, width, height)
            }
        }
        Log.d("CropScale", "running: ")
        return img
    }

    private fun cropDetect(): PngPair {
        val png = getPage()
        val img = fromByteArray(png)
        var roi = Rect()
        var isBlack = false
        if (!img.empty() && doDetect) {
            val (r, b) = cropDetect(img)
            roi = r
            isBlack = b

        }
        Log.d("CropDetect", "running: ")
        return PngPair(png, roi, isBlack)
    }
}
