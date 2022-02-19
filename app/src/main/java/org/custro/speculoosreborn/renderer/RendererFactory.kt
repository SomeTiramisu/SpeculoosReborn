package org.custro.speculoosreborn.renderer

import java.io.File

class RendererFactory {
    companion object {
        fun create(file: File): Renderer {
            if (MangaRenderer.isSupported(file)) { return MangaRenderer(file)}
            //if (PdfRenderer.isSupported(uri)) { return PdfRenderer(uri)}
            throw Exception("Unsupported file")
        }
        fun isSupported(file: File): Boolean {
            return MangaRenderer.isSupported(file)//or
                  //  PdfRenderer.isSupported(uri)*/
        }
    }
}