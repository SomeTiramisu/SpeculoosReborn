package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Rect
import java.util.concurrent.Executors

class CropScaleRunner(private val index: Int, private val parser: Parser) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var mPageResJob: Deferred<PagePair>? = null

    fun preload(req: PageRequest) {
        if (mPageResJob == null) {
            mPageResJob = scope.async(start = CoroutineStart.LAZY) {
                val pngRes: PngPair
                withContext(Dispatchers.IO) {pngRes = cropDetect()}
                cropScale(pngRes, req)
            }
            mPageResJob!!.start()
        }
    }

    suspend fun get(req: PageRequest): PagePair {
        if (mPageResJob == null) {
            preload(req)
        }
        return mPageResJob!!.await()
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
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
        Log.d("CropScale", "running: ${req.index}");
        return PagePair(img, req)
    }

    private fun cropDetect(): PngPair {
        val uri = parser.at(index)
        val img = fromByteArray(PageCache.loadData(uri))
        var roi = Rect()
        var isBlack = false
        if (!img.empty() && index != 0) {
            val (r, b) = cropDetect(img)
            roi = r
            isBlack = b

        }
        Log.d("CropDetect", "running: $index");
        return PngPair(uri, roi, isBlack)
    }
}
