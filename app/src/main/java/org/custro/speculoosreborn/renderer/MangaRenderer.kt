package org.custro.speculoosreborn.renderer

import org.custro.speculoosreborn.parser.Parser
import org.custro.speculoosreborn.parser.ParserFactory
import java.io.File

class MangaRenderer(file: File): Renderer {
    private var parser: Parser = ParserFactory.create(file)

    override val pageCount: Int
        get() = parser.size

    override fun openPage(index: Int): RendererPage {
        //Log.d("MangaRenderer", "opening: $index")
        return MangaRendererPage(parser.at(index))
    }

    override fun close() {
        parser.close()
    }

    companion object {
        fun isSupported(file: File): Boolean {
            return ParserFactory.isSupported(file)
        }
    }
}