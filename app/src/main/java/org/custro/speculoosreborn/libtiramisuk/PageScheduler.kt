package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import java.io.File
import java.util.concurrent.Executors

class PageScheduler {
    private val mImagePreload: Int = 20
    private var mPages: List<CropScaleRunner> = listOf()
    private var mPageSlot: (PagePair) -> Unit = {}
    private var mSizeSlot: (Int) -> Unit = {}
    private var mFile: File? = null
    private var mParser: Parser? = null

    init {
        Log.d("Scheduler", "created")
    }

    fun init(pageCallback: (PagePair) -> Unit, sizeCallback: (Int) -> Unit) {
        mPageSlot = pageCallback
        for (x in mPages) {
            x.connectSlot(mPageSlot)
        }
        mSizeSlot = sizeCallback
        if (mParser != null) {
            mSizeSlot(mParser!!.size)
        }
    }

    fun at(req: PageRequest) {
        if (req.file != mFile) {
            mFile = req.file
            mParser = Parser(mFile!!)
            mSizeSlot(mParser!!.size)
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
                val nreq = PageRequest(i, req.width, req.height, req.file)
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
}