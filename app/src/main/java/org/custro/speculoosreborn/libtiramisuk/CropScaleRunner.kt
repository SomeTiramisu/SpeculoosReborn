package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Mat
import org.opencv.core.Rect

class CropScaleRunner(private val getPage: () -> ByteArray, private val doDetect: Boolean = true, private val doScale: Boolean = true) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var mPageResJob: Deferred<CropPair>? = null

    fun preload() {
        if (mPageResJob == null) {
            mPageResJob = scope.async {
                cropOnly(cropDetect())
            }
            mPageResJob!!.start()
        }
    }

    fun get(width: Int, height: Int): Flow<CropPair> = flow {
        if (mPageResJob == null) {
            clear()
            preload()
        }
        val res = mPageResJob!!.await()
        emit(res)
        //delay(1000)
        emit(CropPair(scaleOnly(res.first, width, height), res.second))
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
    }

    private fun cropOnly(p:  DetectTriple): CropPair {
        val img = fromByteArray(p.first)
        if (!img.empty()) {
            cropProcessNew(img, img, p.second)
        }
        Log.d("CropOnly", "running: ")
        return CropPair(img, p.third)
    }

    private fun scaleOnly(p: Mat, width: Int, height: Int): Mat {
        val img = Mat()
        p.copyTo(img)
        if (!p.empty()) {
            scaleProcessNew(img, img, width, height)
        }
        Log.d("ScaleOnly", "running: ")
        return img
    }

    private fun cropDetect(): DetectTriple {
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
        return DetectTriple(png, roi, isBlack)
    }
}
