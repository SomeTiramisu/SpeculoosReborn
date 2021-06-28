package org.custro.speculoosreborn.libtiramisuk

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.custro.speculoosreborn.libtiramisuk.utils.PngPair
import org.custro.speculoosreborn.libtiramisuk.utils.fromByteArray
import org.opencv.core.Mat
import org.opencv.core.Rect

class CropScaleRunner(parser: Parser) {
    private val mParser = parser
    private var mReq: PageRequest? = null
    private var mPageRes: PagePair? = null
    private var mPngRes: PngPair? = null
    private var mSlot: (PagePair) -> Unit = {}

    private fun runScale() = runBlocking {
        if (mPngRes != null) { //maybe need a .join after launch
            mPngRes = cropDetect(mParser.at(mReq!!.index), mReq!!.index)
        }
        mPageRes = cropScale(mPngRes!!, mReq!!)
        mSlot(mPageRes!!)
    }

    private fun runDetect() = runBlocking { //what if we runscale before completion ?
        mPngRes = cropDetect(mParser.at(mReq!!.index), mReq!!.index)
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
        //TODO
        return PagePair(img, req)
    }

    private suspend fun cropDetect(png: ByteArray, index: Int): PngPair{
        //TODO
        return PngPair(png, Rect())
    }
}
