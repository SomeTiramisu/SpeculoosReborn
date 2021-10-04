package org.custro.speculoosreborn.libtiramisuk.renderer

import android.net.Uri

class RendererFactory {
    companion object {
        fun create(uri: Uri): Renderer {
            if (MangaRenderer.isSupported(uri)) { return MangaRenderer(uri)}
            throw Exception("Unsupported file")
        }
    }
}