package org.custro.speculoosreborn.libtiramisuk.renderer

import android.graphics.Bitmap
import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.utils.*
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Rect

class MangaRendererPage(private val page: ByteArray): RendererPage {
    override fun render(bitmap: Bitmap): RenderInfo {
        val img = fromByteArray(page)
        val detect = cropDetect(img)
        cropScaleProcess(img, img, detect.first, bitmap.width, bitmap.height)
        if (detect.second) {// reenable opencv borders
            addBlackBorders(img, img, bitmap.width, bitmap.height)
        }
        Log.d("MangaRendererPage", "img: w:${img.width()} h: ${img.height()} | btm: w:${bitmap.width} h:${bitmap.height}")
        bitmap.reconfigure(img.width(), img.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(img, bitmap)
        return RenderInfo(detect.second)
    }

    override fun close() {
    }

    private fun cropDetect(img: Mat): Pair<Rect, Boolean> {
        var roi = Rect()
        var isBlack = false
        if (!img.empty()) {
            val (r, b) = org.custro.speculoosreborn.libtiramisuk.utils.cropDetect(img)
            roi = r
            isBlack = b
        }
        Log.d("CropDetect", "running: ")
        return Pair(roi, isBlack)
    }
}