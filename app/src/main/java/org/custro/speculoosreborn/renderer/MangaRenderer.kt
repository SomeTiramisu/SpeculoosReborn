package org.custro.speculoosreborn.renderer

import android.net.Uri
import org.custro.speculoosreborn.parser.Parser
import org.custro.speculoosreborn.parser.ParserFactory

class MangaRenderer(override val uri: Uri): Renderer {
    private var parser: Parser = ParserFactory.create(uri)

    override val pageCount: Int
        get() = parser.size

    override fun openPage(index: Int): RendererPage {
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