package org.custro.speculoosreborn.libtiramisuk

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import org.custro.speculoosreborn.libtiramisuk.renderer.RenderConfig
import org.custro.speculoosreborn.libtiramisuk.renderer.RenderInfo
import org.custro.speculoosreborn.libtiramisuk.renderer.RendererPage

class CropScaleRunner(private val getPage: () -> RendererPage, private val renderConfig: RenderConfig) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var mPageResJob: Deferred<Pair<Bitmap, RenderInfo>>? = null
    private var preloadConfig: Pair<Int, Int>? = null

    fun preload(width: Int, height: Int) {
        if (mPageResJob == null || preloadConfig != Pair(width, height)) {
            clear()
            preloadConfig = Pair(width, height)
            mPageResJob = scope.async {
                val bitmap: Bitmap = if (width == 0 || height == 0) {
                    Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                }
                val renderInfo: RenderInfo
                //renderMutex.withLock { //slow down MangaRenderer, ensure PdfRenderer thread safety. We may remove @Syncronized from Parsers
                    getPage().use {
                        renderInfo = it.render(bitmap, renderConfig)
                //    }
                }
                Pair(bitmap, renderInfo)
            }
            mPageResJob!!.start()
        }
    }

    fun get(width: Int, height: Int): Flow<Pair<Bitmap, RenderInfo>> = flow {
        if (mPageResJob == null || preloadConfig != Pair(width, height)) {
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
