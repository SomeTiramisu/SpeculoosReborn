package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipParser(private val resolver: ContentResolver, override val uri: Uri) : Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        val zipStream = ZipInputStream(getInputStream())
        var count = 0
        while (zipStream.available() == 1) {
            val e = zipStream.nextEntry
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name))) {
                headers.add(Header(count, e.name))
            }
            zipStream.closeEntry()
            count += 1
        }
        zipStream.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })
        headers.sortWith(entryNaturalOrder)
        Log.d("ZipParser", "size: ${headers.size}")
    }

    override fun at(index: Int): ByteArray {
        val zipStream = ZipInputStream(getInputStream())
        for (i in 0 until (headers[index].index)) {
            zipStream.nextEntry
            zipStream.closeEntry()
        }
        val e = zipStream.nextEntry
        val r = zipStream.readBytes()
        Log.d("ZipParser", "Entry size: ${e.size}")

        zipStream.closeEntry()
        zipStream.close()
        return r
    }

    private fun getInputStream() = resolver.openInputStream(uri)

    companion object {
        fun isSupported(uri: Uri) = true
            //uri.lastPathSegment?.lowercase()?.contains(".*\\.(zip|cbz)$") ?: false
    }
}