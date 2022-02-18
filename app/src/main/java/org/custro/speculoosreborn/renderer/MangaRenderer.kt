package org.custro.speculoosreborn.renderer

import android.net.Uri
import android.util.Log
import org.custro.speculoosreborn.parser.Parser
import org.custro.speculoosreborn.parser.ParserFactory

class MangaRenderer(override val uri: Uri): Renderer {
    private var parser: Parser = ParserFactory.create(uri)

    override val pageCount: Int
        get() = parser.size

    override fun openPage(index: Int): RendererPage {
        Log.d("MangaRenderer", "opening: $index")
        return MangaRendererPage(parser.at(index))
    }

    override fun close() {
        parser.close()
    }

    companion object {
        fun isSupported(uri: Uri): Boolean {
            return ParserFactory.isSupported(uri)
        }
    }
}