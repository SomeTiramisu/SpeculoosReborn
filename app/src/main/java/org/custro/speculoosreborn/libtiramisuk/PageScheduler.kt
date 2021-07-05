package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
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
    private var mUri: Uri? = null

    init {
        Log.d("Scheduler", "created")
    }

    fun at(req: PageRequest) {
        if (req.parser!!.uri != mUri) {
            mUri = req.parser.uri
            Log.d("Scheduler", "file: $mUri")
            for (x in mPages) {
                x.finalClear()
            }
            mPages = List(req.parser.size) { index ->
                val r = CropScaleRunner(index)
                r.connectSlot(mPageSlot)
                r
            }
        }
        val index = req.index
        if (index<0 || index >= req.parser.size) {
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
            val preq = PageRequest(req.index + i, req.width, req.height, req.parser)
            val mreq = PageRequest(req.index - i, req.width, req.height, req.parser)
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
}