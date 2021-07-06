package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityDiagnosticsManager
import android.net.Uri
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache


class RarParser(override val uri: Uri) : Parser {
    private val resolver = App.instance!!.contentResolver
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        val rarStream = Archive(getInputStream())
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
        val rar = Archive(getInputStream())
        var e: FileHeader? = null
        for (i in 0..headers[index].index) {
            e = rar.nextFileHeader()
        }
        val iStream = rar.getInputStream(e)
        val r = PageCache.saveData(iStream.readBytes())
        iStream.close()
        rar.close()
        return r
    }

    private fun getInputStream() = resolver.openInputStream(uri)

    companion object {
        fun isSupported(uri: Uri) =
            uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(rar|cbr)$")) ?: false
    }
}