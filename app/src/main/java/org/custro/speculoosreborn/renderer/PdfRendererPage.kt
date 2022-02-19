package org.custro.speculoosreborn.renderer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.util.Log
import org.custro.speculoosreborn.utils.bitmapToMat
import org.custro.speculoosreborn.utils.blackDetect
import org.custro.speculoosreborn.utils.cropDetect
import kotlin.math.min

/*
class PdfRendererPage(private val page: PdfRenderer.Page): RendererPage {
    override fun render(bitmap: Bitmap, config: RenderConfig): RenderInfo {
        val fx = bitmap.width.toDouble()/page.width.toDouble()
        val fy = bitmap.height.toDouble()/page.height.toDouble()
        val f = min(fx, fy)

        val detectBitmap = Bitmap.createBitmap((page.width*f).toInt(), (page.height*f).toInt(), Bitmap.Config.ARGB_8888) //configured with whole page aspect ratio
        page.render(detectBitmap, null, null, RENDER_MODE_FOR_DISPLAY)
        val detectMat = bitmapToMat(detectBitmap)
        val cropRect = cropDetect(detectMat)
        val isBlackBorders = blackDetect(detectMat)
        if (!cropRect.empty()) {
            val f2x = bitmap.width.toDouble() / cropRect.width.toDouble()
            val f2y = bitmap.height.toDouble() / cropRect.height.toDouble()
            val f2 = min(f2x, f2y)

            bitmap.reconfigure((cropRect.width * f2).toInt(), (cropRect.height * f2).toInt(), Bitmap.Config.ARGB_8888) //reconfigure to detected rect aspect ratio
            Log.d("PdfRendererPage", "$f2")
            val f3x = bitmap.width.toFloat()*f2.toFloat() / page.width.toFloat()
            val f3y = bitmap.height.toFloat()*f2.toFloat() / page.height.toFloat()
            val f3 = min(f3x, f3y)
            val trMat = Matrix().apply {
                //postTranslate(-cropRect.x.toFloat(), -cropRect.y.toFloat())
                postScale(f3, f3)
                postTranslate(-cropRect.x.toFloat()*f2.toFloat(), -cropRect.y.toFloat()*f2.toFloat())
            }
            page.render(bitmap, null, trMat, RENDER_MODE_FOR_DISPLAY)
        } else {
            bitmap.reconfigure((page.width*f).toInt(), (page.height*f).toInt(), Bitmap.Config.ARGB_8888)
            //page.render(bitmap, null, Matrix().apply { setScale(15F, 15F);postTranslate(-5000F, -5000F) } , RENDER_MODE_FOR_DISPLAY)
            page.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY)
        }
        return RenderInfo(false)
    }

    override fun close() {
        page.close()
    }
}
*/