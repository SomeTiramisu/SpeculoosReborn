package org.custro.speculoosreborn.libtiramisuk

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.custro.speculoosreborn.libtiramisuk.renderer.RenderInfo
import org.custro.speculoosreborn.libtiramisuk.renderer.Renderer
import org.custro.speculoosreborn.libtiramisuk.utils.CropPair
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.opencv.core.Mat

class PageScheduler(renderer: Renderer) {
    private val mImagePreload: Int = 3
    private var mPages: List<CropScaleRunner>

    init {
        Log.d("Scheduler", "created")
        Log.d("Scheduler", "file: ${renderer.uri}")
        mPages = List(renderer.pageCount) { index ->
            CropScaleRunner({renderer.openPage(index) }, index != 0 )
        }
    }

    fun at(index: Int, width: Int, height: Int): Flow<Pair<Bitmap, RenderInfo>> {
        //Log.d("Scheduler", "get ${req.index}")
        return mPages[index].get(width, height)
    }

    fun seekPagesBouncing(index: Int, width: Int, height: Int) {
        for (i in mPages.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPages[i].clear()
            }
        }
        for (i in 1..mImagePreload) {
            val pi = index + i
            val mi = index - i
            if (pi < mPages.size) mPages[pi].preload(width, height)
            if (mi >= 0) mPages[mi].preload(width, height)
        }
    }

    fun seekPagesOrdered(index: Int, width: Int, height: Int) {
        for (i in mPages.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPages[i].clear()
            }
        }
    for (i in mImagePreload downTo 1) {
        val mi = index - i
        if (mi >= 0) mPages[mi].preload(width, height)
    }
    for (i in 1..mImagePreload) {
        val pi = index + i
        if (pi < mPages.size) mPages[pi].preload(width, height)
    }
    }
}