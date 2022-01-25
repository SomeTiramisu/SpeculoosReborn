package org.custro.speculoosreborn.renderer

import android.graphics.Bitmap
import android.util.Log
import org.custro.speculoosreborn.utils.*
import org.opencv.android.Utils

class MangaRendererPage(private val page: ByteArray): RendererPage {
    override fun render(bitmap: Bitmap, config: RenderConfig): RenderInfo {
        val img = fromByteArray(page)
        val cropRect = cropDetect(img)
        val isBlackBorders = blackDetect(img)
        if(config.doCrop && config.doScale && config.doMask) {
            cropScaleMaskProcess(img, img, cropRect, bitmap.width, bitmap.height)
        } else {
            if(config.doCrop) {
                cropProcessNew(img, img, cropRect)
            }
            if(config.doScale) {
                scaleProcessNew(img, img, bitmap.width, bitmap.height)
            }
            if(config.doMask) {
                maskProcessNew(img, img)
            }
        }
        if (config.addBorders && isBlackBorders) {// reenable opencv borders
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