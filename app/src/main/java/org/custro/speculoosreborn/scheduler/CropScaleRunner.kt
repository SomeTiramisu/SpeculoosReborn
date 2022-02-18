package org.custro.speculoosreborn.scheduler

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.*
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RenderInfo
import org.custro.speculoosreborn.renderer.RendererPage

class CropScaleRunner(
    private val getPage: () -> RendererPage,
    private val renderConfig: RenderConfig
) {
    private val runnerJob = SupervisorJob()
    private val runnerScope = CoroutineScope(Dispatchers.Default + runnerJob)
    private var mPageRes: Pair<Bitmap, RenderInfo>? = null
    private var preloadConfig: Pair<Int, Int>? = null

    fun preload(width: Int, height: Int) {
        Log.d("CropScapeRunner", "preloading")
        if (mPageRes == null || preloadConfig != Pair(width, height)) {
            runnerScope.launch {
                worker(width, height)
            }
        }

    }

    fun get(width: Int, height: Int): Pair<Bitmap, RenderInfo> {
        if (mPageRes == null || preloadConfig != Pair(width, height)) {
            worker(width, height)
        }
        return mPageRes!!
    }

    private fun worker(width: Int, height: Int) {
        Log.d("CropScaleRunner", "working")
        mPageRes = null
        preloadConfig = Pair(width, height)
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
        mPageRes = Pair(bitmap, renderInfo)
    }

    fun clear() {
        runnerJob.cancelChildren()
        mPageRes = null
    }

}