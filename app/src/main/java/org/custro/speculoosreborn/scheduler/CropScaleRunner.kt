package org.custro.speculoosreborn.scheduler

import android.graphics.Bitmap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RenderInfo
import org.custro.speculoosreborn.renderer.RendererPage

class CropScaleRunner(
    private val getPage: () -> RendererPage,
    private val renderConfig: RenderConfig
) {
    private var mPageResJob: Deferred<Pair<Bitmap, RenderInfo>>? = null
    private var preloadConfig: Pair<Int, Int>? = null

    suspend fun preload(width: Int, height: Int) = coroutineScope {
        if (mPageResJob == null || preloadConfig != Pair(width, height)) {
            clear()
            preloadConfig = Pair(width, height)
            mPageResJob = async {
                val bitmap: Bitmap = if (width == 0 || height == 0) {
                    Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                }
                val renderInfo: RenderInfo
                //slow down MangaRenderer, ensure PdfRenderer thread safety. We may remove @Syncronized from Parsers
                getPage().use {
                    renderInfo = it.render(bitmap, renderConfig)
                }
                Pair(bitmap, renderInfo)
            }
        }
    }

    suspend fun get(width: Int, height: Int): Pair<Bitmap, RenderInfo> = coroutineScope {
        if (mPageResJob == null || preloadConfig != Pair(width, height)) {
            clear()
            preload(width, height)
        }
        mPageResJob!!.await()
    }

    fun clear() {
        mPageResJob?.cancel()
        mPageResJob = null
    }

}