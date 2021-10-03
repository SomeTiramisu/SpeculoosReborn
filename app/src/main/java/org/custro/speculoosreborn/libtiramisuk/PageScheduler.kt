package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.custro.speculoosreborn.libtiramisuk.utils.CropPair
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.opencv.core.Mat

class PageScheduler(mangaParser: MangaParser) {
    private val mImagePreload: Int = 3
    private var mPages: List<CropScaleRunner>

    init {
        Log.d("Scheduler", "created")
        Log.d("Scheduler", "file: ${mangaParser.uri}")
        mPages = List(mangaParser.size) { index ->
            CropScaleRunner({mangaParser.at(index) }, index != 0 )
        }
    }

    fun at(index: Int, width: Int, height: Int): Flow<CropPair> {
        //Log.d("Scheduler", "get ${req.index}")
        return mPages[index].get(width, height)
    }

    fun seekPagesBouncing(index: Int) {
        for (i in mPages.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPages[i].clear()
            }
        }
        for (i in 1..mImagePreload) {
            val pi = index + i
            val mi = index - i
            if (pi < mPages.size) mPages[pi].preload()
            if (mi >= 0) mPages[mi].preload()
        }
    }

    fun seekPagesOrdered(index: Int) {
        for (i in mPages.indices) {
            if ((index - mImagePreload > i) || (i > index + mImagePreload)) {
                mPages[i].clear()
            }
        }
    for (i in mImagePreload downTo 1) {
        val mi = index - i
        if (mi >= 0) mPages[mi].preload()
    }
    for (i in 1..mImagePreload) {
        val pi = index + i
        if (pi < mPages.size) mPages[pi].preload()
    }
    }
}