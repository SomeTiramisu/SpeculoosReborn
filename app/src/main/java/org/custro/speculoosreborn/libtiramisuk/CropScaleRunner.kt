package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Rect
import java.util.concurrent.Executors

class CropScaleRunner(private val index: Int, private val parser: Parser) {
    private var mPageRes: PagePair? = null
    private var mPageResJob: Job? = null
    private var mPngRes: PngPair? = null
    private var mPngResJob: Job? = null
    private var mReq: PageRequest? = null


    suspend fun preload(req: PageRequest) {
        if (mPngRes == null || mPngResJob == null) {
            mPngResJob = coroutineScope {
                launch(Dispatchers.IO) {
                    mPngRes = cropDetect()
                }
            }
        }
        mPngResJob!!.join()
        if (mReq != req || mPageRes == null || mPageResJob == null) {
            mReq = req
            mPageResJob = coroutineScope {
                launch {
                    mPageRes = cropScale(mPngRes!!, req)
                }
            }
        }
    }

    suspend fun get(req: PageRequest): PagePair {
        preload(req)
        mPageResJob!!.join()
        return mPageRes!!
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
        mPageRes = null
    }

    fun finalClear() {
        mPageResJob?.cancel()
        mPngResJob?.cancel()
    }

    private fun cropScale(p: PngPair, req: PageRequest): PagePair {
        val img = fromByteArray(PageCache.loadData(p.uri))
        if (!img.empty()) {
            cropScaleProcess(img, img, p.rec, req.width, req.height)
            if (p.isBlack) {
                //Log.d("Runner", "is black")
                addBlackBorders(img, img, req.width, req.height)
            }
        }
        Log.d("CropScale", "running: ${req.index}");
        return PagePair(img, req)
    }

    private fun cropDetect(): PngPair {
        val uri = parser.at(index)
        val img = fromByteArray(PageCache.loadData(uri))
        var roi = Rect()
        var isBlack = false
        if (!img.empty() && index != 0) {
            val (r, b) = cropDetect(img)
            roi = r
            isBlack = b

        }
        Log.d("CropDetect", "running: $index");
        return PngPair(uri, roi, isBlack)
    }
}
