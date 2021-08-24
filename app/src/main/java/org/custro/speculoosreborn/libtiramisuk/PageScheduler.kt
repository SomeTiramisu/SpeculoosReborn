package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest

class PageScheduler {
    private val mImagePreload: Int = 20
    private var mPages: List<CropScaleRunner> = listOf()
    private var mUri: Uri = Uri.EMPTY

    init {
        Log.d("Scheduler", "created")
    }

    suspend fun at(req: PageRequest): PagePair {
        if (req.uri != mUri) {
            mUri = req.uri
            Log.d("Scheduler", "file: ${req.uri}")
            val parser = ParserFactory.create(mUri)
            for (x in mPages) {
                x.finalClear()
            }
            mPages = List(parser!!.size) { index ->
                CropScaleRunner(index, parser)
            }
        }
        //Log.d("Scheduler", "get ${req.index}")

        seekPages(req)
        return mPages[req.index].get(req)
    }

    private suspend fun seekPages(req: PageRequest) {
        coroutineScope {
            launch {
                for (i in mPages.indices) {
                    if ((req.index - mImagePreload > i) || (i > req.index + mImagePreload)) {
                        mPages[i].clear()
                    }
                }
                for (i in 1..mImagePreload) {
                    val preq = PageRequest(req.index + i, req.width, req.height, req.uri)
                    val mreq = PageRequest(req.index - i, req.width, req.height, req.uri)
                    if (preq.index < mPages.size) mPages[preq.index].preload(preq)
                    if (mreq.index >= 0) mPages[mreq.index].preload(mreq)
                }
            }
        }
    }

}