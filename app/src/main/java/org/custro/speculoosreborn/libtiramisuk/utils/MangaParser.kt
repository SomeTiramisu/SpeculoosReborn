package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import org.custro.speculoosreborn.room.Manga

class MangaParser(val uri: Uri) {
    var localUri: Uri = PageCache.saveData(uri)
    private var parser: Parser = ParserFactory.create(localUri)
    val size get() = parser.size
    val cover
        get() = run {
            val mat = fromByteArray(parser.at(0))
            scale(mat, mat, 200, 200)
            toByteArray(mat)
        }

    fun at(index: Int): ByteArray {
        return parser.at(index)
    }

    val manga get() = Manga(uri.toString())
}