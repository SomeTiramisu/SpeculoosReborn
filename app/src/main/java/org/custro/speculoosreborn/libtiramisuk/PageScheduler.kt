package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import java.io.File

class PageScheduler {
    private val mImagePreload: Int = 5
    private val mPages = mutableListOf<CropScaleRunner>()
    private var mPageSlot: (PagePair) -> Unit = {}
    private var mSizeSlot: (Int) -> Unit = {}
    private var mFile: File? = null
    private var mParser: Parser? = null

    init {
        Log.d("Scheduler", "created")
    }

    fun at(req: PageRequest) {
        if (req.file != mFile) {
            mFile = req.file
            mParser = Parser(mFile!!)
            mSizeSlot(mParser!!.size)
            for (i in 0 until mParser!!.size) {
                mPages.add(CropScaleRunner(mParser!!))
                mPages[i].connectSlot(mPageSlot)
            }
        }
        val index = req.index
        val bookSize = mParser!!.size
        if (index<0 || index >= bookSize) {
            mPageSlot(PagePair())
            return
        }
        Log.d("Scheduler", "get ${req.index}")
        mPages[index].get(req)
        seekPages(req)
    }

    fun connectPageSlot(slot: (PagePair) -> Unit) {
        mPageSlot = slot
    }

    fun connectSizeSlot(slot: (Int) -> Unit) {
        mSizeSlot = slot
    }

    private fun seekPages(req: PageRequest) {
        for (i in 0 until mPages.size) {
            val nreq = PageRequest(i, req.width, req.height, req.file)
            if ((req.index - mImagePreload <= i) && (i <= req.index + mImagePreload)) {
                mPages[i].get(nreq)
            } else {
                mPages[i].preload(nreq)
                mPages[i].clear()
            }
        }
    }
}