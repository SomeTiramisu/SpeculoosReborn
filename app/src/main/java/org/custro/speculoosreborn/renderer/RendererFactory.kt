package org.custro.speculoosreborn.renderer

import android.net.Uri

class RendererFactory {
    companion object {
        fun create(uri: Uri): Renderer {
            if (MangaRenderer.isSupported(uri)) { return MangaRenderer(uri)}
            if (PdfRenderer.isSupported(uri)) { return PdfRenderer(uri)}
            throw Exception("Unsupported file")
        }
        fun isSupported(uri: Uri): Boolean {
            return MangaRenderer.isSupported(uri) or
                    PdfRenderer.isSupported(uri)
        }
    }
}