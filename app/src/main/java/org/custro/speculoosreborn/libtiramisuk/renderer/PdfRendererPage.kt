package org.custro.speculoosreborn.libtiramisuk.renderer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY

class PdfRendererPage(private val page: PdfRenderer.Page): RendererPage {
    override fun render(bitmap: Bitmap): RenderInfo {
        page.render(bitmap, null, null , RENDER_MODE_FOR_DISPLAY)
        return RenderInfo(false)
    }

    override fun close() {
        page.close()
    }
}