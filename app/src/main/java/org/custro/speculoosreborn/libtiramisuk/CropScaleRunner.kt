package org.custro.speculoosreborn.libtiramisuk

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.custro.speculoosreborn.libtiramisuk.renderer.RenderInfo
import org.custro.speculoosreborn.libtiramisuk.renderer.RendererPage
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.core.Mat
import org.opencv.core.Rect

class CropScaleRunner(private val getPage: () -> RendererPage, private val doDetect: Boolean = true, private val doScale: Boolean = true) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var mPageResJob: Deferred<Pair<Bitmap, RenderInfo>>? = null

    fun preload(width: Int, height: Int) {
        if (mPageResJob == null) {
            mPageResJob = scope.async {
                val bitmap: Bitmap = if (width == 0 || height == 0) {
                    Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                }
                val renderInfo: RenderInfo
                renderMutex.withLock { //slow down MangaRenderer, ensure PdfRenderer thread safety. We may remove @Syncronized from Parsers
                    getPage().use {
                        renderInfo = it.render(bitmap)
                    }
                }
                Pair(bitmap, renderInfo)
            }
            mPageResJob!!.start()
        }
    }

    fun get(width: Int, height: Int): Flow<Pair<Bitmap, RenderInfo>> = flow {
        if (mPageResJob == null) {
            clear()
            preload(width, height)
        }
        val res = mPageResJob!!.await()
        emit(res)
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
    }

    companion object {
        val renderMutex = Mutex()
    }
}
