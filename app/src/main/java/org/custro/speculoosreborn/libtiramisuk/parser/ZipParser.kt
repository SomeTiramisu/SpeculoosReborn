package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipParser(override val uri: Uri) : Parser {
    private val resolver = App.instance!!.contentResolver
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
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

    override fun at(index: Int): Uri {
        val zipStream = ZipInputStream(getInputStream())
        var entry: ZipEntry = zipStream.nextEntry
        for (i in 0 until headers[index].index) {
            entry = zipStream.nextEntry
            //zipStream.closeEntry()
        }
        //Log.d("ZipParser", entry.size.toString())
        //val buffer = zipStream.readBytes()
        //val r = PageCache.saveData(buffer)
        val r = PageCache.saveData(zipStream)
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