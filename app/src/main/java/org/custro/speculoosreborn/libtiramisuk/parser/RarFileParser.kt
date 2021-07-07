package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityDiagnosticsManager
import android.net.Uri
import androidx.core.net.toFile
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache


class RarFileParser(override val uri: Uri) : Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        val rarStream = Archive(uri.toFile())
        var e: FileHeader? = rarStream.nextFileHeader()
        var count = 0
        while (e != null) {
            val name = e.fileName
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name))) {
                headers.add(Header(count, e.fileName))
            }
            e = rarStream.nextFileHeader()
            count += 1
        }
        rarStream.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })

        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): Uri {
        val rar = Archive(uri.toFile())
        var e: FileHeader? = null
        for (i in 0..headers[index].index) {
            e = rar.nextFileHeader()
        }
        val iStream = rar.getInputStream(e)
        val r = PageCache.saveData(iStream)
        iStream.close()
        rar.close()
        return r
    }

    companion object {
        fun isSupported(uri: Uri) = uri.scheme == "file"
                && uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(rar|cbr)$")) ?: false
    }
}