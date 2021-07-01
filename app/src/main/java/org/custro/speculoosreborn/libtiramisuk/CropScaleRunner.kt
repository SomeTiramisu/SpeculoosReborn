package org.custro.speculoosreborn.libtiramisuk

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
    private var mSlot: (PagePair) -> Unit = {}

    init {
        mPngResJob = detectCoScope.launch {
            if (mPngRes == null) {
                mPngRes = cropDetect(parser.at(index), index)
            }
        }
    }

    fun get(req: PageRequest) = scaleCoScope.launch {
        if (mPngResJob == null) {
            TODO()
        } else {
            mPngResJob!!.join()
        }

        if (mReq != req || mPageRes == null) {
            //Log.d("Runner", "Request it ${req.index}")
            mReq = req
            mPageRes = cropScale(mPngRes!!, req)
        }
        //Log.d("Runner", "Have it ${req.index}")
        mSlot(mPageRes!!)
    }

    fun clear() {
        mPageRes = null
    }

    fun connectSlot(slot: (PagePair) -> Unit) {
        mSlot = slot
    }

    private fun cropScale(p: PngPair, req: PageRequest): PagePair {
        val img = fromByteArray(p.png)
        if (!img.empty()) {
            cropScaleProcess(img, img, p.rec, req.width, req.height)
            if (p.isBlack) {
                //Log.d("Runner", "is black")
                //addBlackBorders(img, img, req.width, req.height)
            }
        }
        //Log.d("CropScale", "running: ${req.index}");
        return PagePair(img, req)
    }

    private fun cropDetect(png: ByteArray, index: Int): PngPair{
        val img = fromByteArray(png)
        var roi = Rect()
        var isBlack = false
        if (!img.empty() && index != 0) {
            var (r, b) = cropDetect(img)
            roi = r
            isBlack = b

        }
        //Log.d("CropDetect", "running: $index");
        return PngPair(png, roi, isBlack)
    }

    companion object {
        private val scaleCoScope = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
        private val detectCoScope = CoroutineScope(Executors.newFixedThreadPool(2).asCoroutineDispatcher())
    }
}
