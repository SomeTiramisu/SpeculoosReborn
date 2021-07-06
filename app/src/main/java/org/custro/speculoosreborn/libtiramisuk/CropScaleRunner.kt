package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
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
    private var mSlot: (PagePair) -> Unit = {Log.d("Runner", "empty PageSlot")}

    init {
        mPngResJob = detectCoScope.launch {
            if (mPngRes == null) {
                mPngRes = cropDetect(parser.at(index), index)
            }
        }
    }

    private suspend fun work(req: PageRequest) {
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
    }

    fun get(req: PageRequest)  = getScaleCoScope.launch{
        if (mPageResJob == null || mReq != req) {
            Log.d("Runner", "request it ${req.index}")
            work(req)
            mSlot(mPageRes!!)
        } else {
            Log.d("Runner", "Have it or already requested ${req.index}")
            mPageResJob!!.join()
            mSlot(mPageRes!!)
        }
    }

    fun preload(req: PageRequest) {
        if (mPageResJob == null) {
            mPageResJob = preScaleCoScope.launch {
                work(req)
            }
        }
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

    fun connectSlot(slot: (PagePair) -> Unit) {
        mSlot = slot
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
        //Log.d("CropScale", "running: ${req.index}");
        return PagePair(img, req)
    }

    private fun cropDetect(uri: Uri, index: Int): PngPair{
        val img = fromByteArray(PageCache.loadData(uri))
        var roi = Rect()
        var isBlack = false
        if (!img.empty() && index != 0) {
            val (r, b) = cropDetect(img)
            roi = r
            isBlack = b

        }
        //Log.d("CropDetect", "running: $index");
        return PngPair(uri, roi, isBlack)
    }

    companion object {
        private val getScaleCoScope = CoroutineScope(Executors.newFixedThreadPool(2).asCoroutineDispatcher())
        private val preScaleCoScope = CoroutineScope(Executors.newFixedThreadPool(4).asCoroutineDispatcher())
        private val detectCoScope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    }
}
