package org.custro.speculoosreborn.renderer

import org.custro.speculoosreborn.parser.Parser
import org.custro.speculoosreborn.parser.ParserFactory
import java.io.File
import java.io.InputStream

class MangaRenderer: Renderer {
     constructor(file: File) {
         parser = ParserFactory.create(file)
     }
    constructor(getInputStream: () -> InputStream) {
        parser = ParserFactory.create(getInputStream)
    }

    private var parser: Parser

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
        fun isSupported(getInputStream: () -> InputStream): Boolean {
            return ParserFactory.isSupported(getInputStream)
        }
    }
}