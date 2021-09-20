package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.custro.speculoosreborn.libtiramisuk.parser.ParserFactory
import java.io.Closeable

class MangaParser(override val uri: Uri): Parser, Closeable, AutoCloseable {
    private var parser: Parser = ParserFactory.create(uri)
    override val size get() = parser.size
    val cover: Uri by lazy  {
        val mat = fromByteArray(this.at(0))
        scale(mat, mat, 200, 200)
        //matToBitmap(mat).asImageBitmap()
        Log.d("MangaParser", "${mat.size()}")
        PageCache.saveData(toByteArray(mat), ".png")
    }

    override fun at(index: Int): ByteArray {
        return parser.at(index)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun close() {
        parser.close()
    }
}