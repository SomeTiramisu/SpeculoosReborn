package org.custro.speculoosreborn.parser

import android.net.Uri
import android.util.Log
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.utils.AlphanumComparator
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipBStreamParser(override val uri: Uri) : Parser {
    private val resolver = App.instance.contentResolver
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        Log.d("ZipParser", "scheme: ${uri.scheme}")
        val zipStream = ZipInputStream(getInputStream())
        var e: ZipEntry? = zipStream.nextEntry
        var count = 0
        while (e != null) {
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name))) {
                headers.add(Header(count, e.name))
            }
            e = zipStream.nextEntry
            count += 1
        }
        zipStream.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })
        headers.sortWith(entryNaturalOrder)
        //Log.d("ZipParser", "size: ${headers.size}")
    }

    override fun at(index: Int): ByteArray {
        val inBStream = BufferedInputStream(getInputStream())
        val zipStream = ZipInputStream(inBStream)
        for (i in 0..headers[index].index) {
            zipStream.nextEntry
            //zipStream.closeEntry()
        }
        val buffer = ByteArray(2048)
        val outAStream = ByteArrayOutputStream()
        val outBStream = BufferedOutputStream(outAStream)
        var n = zipStream.read(buffer, 0, 2048)
        while (n >= 0) {
            outBStream.write(buffer, 0, n)
            n = zipStream.read(buffer, 0, 2048)
        }
        zipStream.close()
        inBStream.close()
        outBStream.close()
        return outAStream.toByteArray()
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    private fun getInputStream() =
        resolver.openInputStream(uri)

    companion object {
        //fun isSupported(uri: Uri) =
        //    uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(zip|cbz)$")) ?: false
        fun isSupported(uri: Uri): Boolean {
            val buf = ByteArray(4)
            App.instance.contentResolver.openInputStream(uri)!!.read(buf)
            val magic = ByteBuffer.wrap(buf).int
            return magic == 0x504B0304 || magic == 0x504B0506
        }
    }
}