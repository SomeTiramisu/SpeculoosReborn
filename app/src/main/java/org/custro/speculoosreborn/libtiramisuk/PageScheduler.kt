package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import java.io.File

class PageScheduler {
    private val mImagePreload: Int = 20
    private var mPages: List<CropScaleRunner> = listOf()
    private var mPageSlot: (PagePair) -> Unit = {Log.d("Scheduler", "empty PageSlot")}
    private var mSizeSlot: (Int) -> Unit = {Log.d("Scheduler", "empty SizeSlot")}
    private var mFile: File? = null
    private var mParser: Parser? = null

    init {
        Log.d("Scheduler", "created")
    }

    fun at(req: PageRequest) {
        if (req.file != mFile) {
            mFile = req.file
            Log.d("Scheduler", "file: ${req.file!!.path}")
            mParser = ParserFactory.create(mFile!!)
            mSizeSlot(mParser!!.size)
            for (x in mPages) {
                x.finalClear()
            }
            mPages = List(mParser!!.size) { index ->
                val r = CropScaleRunner(index, mParser!!)
                r.connectSlot(mPageSlot)
                r
            }
        }
        val index = req.index
        val bookSize = mParser!!.size
        if (index<0 || index >= bookSize) {
            return
        }
        //Log.d("Scheduler", "get ${req.index}")
        mPages[index].get(req)
        seekPages(req)
    }

    private fun seekPages(req: PageRequest) {
        for (i in mPages.indices) {
            if ((req.index - mImagePreload > i) || (i > req.index + mImagePreload)) {
                mPages[i].clear()
            }
        }
        for (i in 1..mImagePreload) {
            val preq = PageRequest(req.index + i, req.width, req.height, req.file)
            val mreq = PageRequest(req.index - i, req.width, req.height, req.file)
            if (preq.index < mPages.size) mPages[preq.index].preload(preq)
            if (mreq.index >= 0) mPages[mreq.index].preload(mreq)
        }
    }

    fun connectPageCallback(pageCallback: (PagePair) -> Unit) {
        mPageSlot = pageCallback
        for (x in mPages) {
            x.connectSlot(mPageSlot)
        }
    }
    fun connectSizeCallback(sizeCallback: (Int) -> Unit) {
        mSizeSlot = sizeCallback
        if (mParser != null) {
            mSizeSlot(mParser!!.size)
        }
    }
}