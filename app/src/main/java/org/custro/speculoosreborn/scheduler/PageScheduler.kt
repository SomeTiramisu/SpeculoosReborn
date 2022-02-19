package org.custro.speculoosreborn.scheduler

import android.graphics.Bitmap
import android.util.Log
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RenderInfo
import org.custro.speculoosreborn.renderer.Renderer

class PageScheduler(renderer: Renderer) {
    private val mImagePreload: Int = 3
    private val mPageRunners: List<CropScaleRunner> = List(renderer.pageCount) { index ->
        val config = RenderConfig(
            addBorders = index != 0,
            doScale = true,
            doCrop = index != 0,
            doMask = true
        )
        CropScaleRunner({ renderer.openPage(index) }, config)
    }

    init {
        //Log.d("Scheduler", "New scheduler with file: ${renderer.uri}")
    }

    //TODO: using withContext(Dispatches.IO) alone fail on second attempt, seems not related to mPageRunners
    //NOTE: on second opening
    //    = withContext(Dispatches.IO) { blocking } fail
    //    = coroutineScope {blocking} success
    //    = coroutineScope {withContext(Dispatches.IO) {blocking}} success
    fun at(index: Int, width: Int, height: Int): Pair<Bitmap, RenderInfo> {
        Log.d("Scheduler", "get $index")
        return mPageRunners[index].get(width, height)
    }


    fun seekPagesBouncing(index: Int, width: Int, height: Int) {
        for (i in mPageRunners.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPageRunners[i].clear()
            }
        }
        for (i in 1..mImagePreload) {
            val pi = index + i
            val mi = index - i
            if (pi < mPageRunners.size) mPageRunners[pi].preload(width, height)
            if (mi >= 0) mPageRunners[mi].preload(width, height)
        }
    }

    fun seekPagesOrdered(index: Int, width: Int, height: Int) {
        for (i in mPageRunners.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPageRunners[i].clear()
            }
        }
        for (i in mImagePreload downTo 1) {
            val mi = index - i
            if (mi >= 0) mPageRunners[mi].preload(width, height)
        }
        for (i in 1..mImagePreload) {
            val pi = index + i
            if (pi < mPageRunners.size) mPageRunners[pi].preload(width, height)
        }
    }
}