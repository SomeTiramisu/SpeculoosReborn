package org.custro.speculoosreborn.scheduler

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RenderInfo
import org.custro.speculoosreborn.renderer.Renderer

class PageScheduler(renderer: Renderer) {
    private val mImagePreload: Int = 3
    private var mPageRunners: List<CropScaleRunner>

    init {
        Log.d("Scheduler", "New scheduler with file: ${renderer.uri}")
        mPageRunners = List(renderer.pageCount) { index ->
            val config = RenderConfig(
                addBorders = index != 0,
                doScale = true,
                doCrop = index != 0,
                doMask = true
            )
            CropScaleRunner({ renderer.openPage(index) }, config)
        }
    }
    //TODO: using withContext(Dispatches.IO) alone fail on second attempt, seems not related to mPageRunners
    //NOTE: on second opening
    //    = withContext(Dispatches.IO) { blocking } fail
    //    = coroutineScope {blocking} success
    //    = coroutineScope {withContext(Dispatches.IO) {blocking}} success
    suspend fun at(index: Int, width: Int, height: Int): Pair<Bitmap, RenderInfo> = coroutineScope {
        Log.d("Scheduler", "get $index")
        withContext(Dispatchers.IO) {
            mPageRunners[index].get(width, height)
        }
    }


    suspend fun seekPagesBouncing(index: Int, width: Int, height: Int) = withContext(Dispatchers.Default) {
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

    suspend fun seekPagesOrdered(index: Int, width: Int, height: Int) = withContext(Dispatchers.Default) {
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