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
import java.util.zip.ZipFile
import java.util.zip.ZipFile.OPEN_READ
import java.util.zip.ZipInputStream


class ZipFileParser(override val uri: Uri) : Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        Log.d("ZipFileParser", "scheme: ${uri.scheme}")
        val zipFile = ZipFile(uri.toFile(), OPEN_READ)
        var count = 0
        for (entry in zipFile.entries()) {
            val name = entry.name
            if (!entry.isDirectory and ((".jpg" in name) or (".png" in name))) {
                headers.add(Header(count, entry.name))
            }
            count += 1
        }
        zipFile.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })
        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): Uri {
        val zipFile = ZipFile(uri.toFile(), OPEN_READ)
        val entryInputStream = zipFile.getInputStream(zipFile.getEntry(headers[index].filename))
        val r = PageCache.saveData(entryInputStream)
        entryInputStream.close()
        zipFile.close()
        return r
    }

    companion object {
        fun isSupported(uri: Uri) = uri.scheme == "file"
                && uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(zip|cbz)$")) ?: false
    }
}