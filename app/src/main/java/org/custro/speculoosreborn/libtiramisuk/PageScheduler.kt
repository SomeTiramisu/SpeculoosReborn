package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class PageScheduler {
    private val mImagePreload: Int = 20
    private var mPages: List<CropScaleRunner> = listOf()

    init {
        Log.d("Scheduler", "created")
    }

    fun open(uri: Uri): Int {
        Log.d("Scheduler", "file: $uri")
        val localUri = PageCache.saveData(uri)
        val parser = ParserFactory.create(localUri)
        for (x in mPages) {
            x.finalClear()
        }
        mPages = List(parser!!.size) { index ->
            CropScaleRunner(index, parser)
        }
        return parser.size
    }

    suspend fun at(req: PageRequest): Mat {
        //Log.d("Scheduler", "get ${req.index}")
        return mPages[req.index].get(req).img
    }

    suspend fun seekPages(req: PageRequest) {
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