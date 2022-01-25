package org.custro.speculoosreborn.renderer

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import org.custro.speculoosreborn.App

class PdfRenderer(override val uri: Uri) : Renderer {
    private val pdfFileDescriptor = App.instance.contentResolver.openFileDescriptor(uri, "r")!!
    private val pdfRenderer = PdfRenderer(pdfFileDescriptor)

    override val pageCount: Int
        get() = pdfRenderer.pageCount

    override fun openPage(index: Int): RendererPage {
        return PdfRendererPage(pdfRenderer.openPage(index))
    }

    override fun close() {
        pdfRenderer.close()
        pdfFileDescriptor.close()
    }

    companion object {
        fun isSupported(uri: Uri) =
            uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(pdf)$")) ?: false
    }
}
