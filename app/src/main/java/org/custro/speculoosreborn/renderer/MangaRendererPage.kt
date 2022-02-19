package org.custro.speculoosreborn.renderer

import android.util.Log
import org.custro.speculoosreborn.utils.*
import org.opencv.core.Mat

class MangaRendererPage(private val page: ByteArray): RendererPage {
    override fun render(img: Mat, width: Int, height: Int, config: RenderConfig): RenderInfo {
        fromByteArray(page).copyTo(img)
        val cropRect = cropDetect(img)
        val isBlackBorders = blackDetect(img)
        if(config.doCrop && config.doScale && config.doMask) {
            cropScaleMaskProcess(img, img, cropRect, width, height)
        } else {
            if(config.doCrop) {
                cropProcessNew(img, img, cropRect)
            }
            if(config.doScale) {
                scaleProcessNew(img, img, width, height)
            }
            if(config.doMask) {
                maskProcessNew(img, img)
            }
        }
        if (config.addBorders && isBlackBorders) {// reenable opencv borders
            addBlackBorders(img, img, width, height)
        }
        Log.d("MangaRendererPage", "img: w:${img.width()} h: ${img.height()} | btm: w:${width} h:${height}")
        //bitmap.reconfigure(img.width(), img.height(), Bitmap.Config.ARGB_8888)
        //Utils.matToBitmap(img, bitmap)
        return RenderInfo(isBlackBorders)
    }

    override fun close() {
    }
}