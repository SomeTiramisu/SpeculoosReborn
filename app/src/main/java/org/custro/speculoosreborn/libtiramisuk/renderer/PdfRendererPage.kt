package org.custro.speculoosreborn.libtiramisuk.renderer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import kotlin.math.min

class PdfRendererPage(private val page: PdfRenderer.Page): RendererPage {
    override fun render(bitmap: Bitmap): RenderInfo {
        val fx = bitmap.width.toDouble()/page.width.toDouble()
        val fy = bitmap.height.toDouble()/page.height.toDouble()
        val f = min(fx, fy)
        bitmap.reconfigure((page.width*f).toInt(), (page.height*f).toInt(), Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, Matrix().apply { setScale(15F, 15F);postTranslate(-5000F, -5000F) } , RENDER_MODE_FOR_DISPLAY)
        return RenderInfo(false)
    }

    override fun close() {
        page.close()
    }
}