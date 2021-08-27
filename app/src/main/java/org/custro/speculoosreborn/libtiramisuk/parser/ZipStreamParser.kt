package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipStreamParser(override val uri: Uri) : Parser {
    private val resolver = App.instance!!.contentResolver
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
        val zipStream = ZipInputStream(getInputStream())
        for (i in 0..headers[index].index) {
            zipStream.nextEntry
            //zipStream.closeEntry()
        }
        //Log.d("ZipParser", entry.size.toString())
        //val buffer = zipStream.readBytes()
        //val r = PageCache.saveData(buffer)
        val r = zipStream.readBytes()
        zipStream.close()
        return r
    }

    private fun getInputStream() =
        if (uri.scheme == "file") uri.toFile().inputStream() else resolver.openInputStream(uri)

    companion object {
        fun isSupported(uri: Uri) =
            uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(zip|cbz)$")) ?: false
    }
}