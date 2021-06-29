package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Rect

class CropScaleRunner(private val parser: Parser, private val coroutineScope: CoroutineScope) {
    private var mReq: PageRequest? = null
    private var mPageRes: PagePair? = null
    private var mPngRes: PngPair? = null
    private var mSlot: (PagePair) -> Unit = {}
    //private var mCoroutineScope = CoroutineScope(Dispatchers.Default)

    private fun runScale() = coroutineScope.launch {
        if (mPngRes == null) { //maybe need a .join after launch
            mPngRes = cropDetect(parser.at(mReq!!.index), mReq!!.index)
        }
        mPageRes = cropScale(mPngRes!!, mReq!!)
        mSlot(mPageRes!!)
    }

    private fun runDetect() = coroutineScope.launch { //what if we runscale before completion ?
        mPngRes = cropDetect(parser.at(mReq!!.index), mReq!!.index)
    }


    fun get(req: PageRequest) {
        if (mReq != req || mPageRes == null) {
            mReq = req
            runScale()
            return
        }
        mSlot(mPageRes!!)
    }

    fun preload(req: PageRequest) {
        if (mPngRes == null) {
            mReq = req
            runDetect()
        }
    }

    fun clear() {
        mPageRes = null
    }

    fun connectSlot(slot: (PagePair) -> Unit) {
        mSlot = slot
    }

    private suspend fun cropScale(p: PngPair, req: PageRequest): PagePair {
        val img = fromByteArray(p.png)
        if (!img.empty()) {
            cropScaleProcess(img, img, p.rec, req.width, req.height)
        }
        Log.d("CropScale", "running: ${req.index}");
        return PagePair(img, req)
    }

    private suspend fun cropDetect(png: ByteArray, index: Int): PngPair{
        val img = fromByteArray(png)
        var roi = Rect()
        if (!img.empty() && index != 0) {
            roi = cropDetect(img)
        }
        Log.d("CropDetect", "running: ${index}");
        return PngPair(png, roi)
    }
}
