package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Rect

class CropScaleRunner(private val parser: Parser, private val scaleCoScope: CoroutineScope, private val detectCoScope: CoroutineScope) {
    private var mReq: PageRequest? = null
    private var mPageRes: PagePair? = null
    private var mPngRes: PngPair? = null
    private var mSlot: (PagePair) -> Unit = {}

    private fun runScale() {
        if (mPngRes == null) { //maybe need a .join after launch
            mPngRes = cropDetect(parser.at(mReq!!.index), mReq!!.index)
        }
        mPageRes = cropScale(mPngRes!!, mReq!!)
        mSlot(mPageRes!!)
    }

    private fun runDetect() { //what if we runscale before completion ?
        mPngRes = cropDetect(parser.at(mReq!!.index), mReq!!.index)
    }


    fun get(req: PageRequest) = scaleCoScope.launch {
        if (mReq != req || mPageRes == null) {
            //Log.d("Runner", "Request it ${req.index}")
            mReq = req
            runScale()
        } else {
            //Log.d("Runner", "Have it ${req.index}")
            mSlot(mPageRes!!)
        }
    }


    fun preload(req: PageRequest) = detectCoScope.launch {
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

    private fun cropScale(p: PngPair, req: PageRequest): PagePair {
        val img = fromByteArray(p.png)
        if (!img.empty()) {
            cropScaleProcess(img, img, p.rec, req.width, req.height)
            if (p.isBlack) {
                Log.d("Runner", "is black")
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
}
