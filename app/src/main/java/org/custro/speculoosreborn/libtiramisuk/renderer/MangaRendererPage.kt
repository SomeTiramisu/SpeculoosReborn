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
        val cropRect = cropDetect(img)
        val isBlackBorders = blackDetect(img)
        cropScaleMaskProcess(img, img, cropRect, bitmap.width, bitmap.height)
        if (isBlackBorders) {// reenable opencv borders
            addBlackBorders(img, img, bitmap.width, bitmap.height)
        }
        Log.d("MangaRendererPage", "img: w:${img.width()} h: ${img.height()} | btm: w:${bitmap.width} h:${bitmap.height}")
        bitmap.reconfigure(img.width(), img.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(img, bitmap)
        return RenderInfo(isBlackBorders)
    }

    override fun close() {
    }
}