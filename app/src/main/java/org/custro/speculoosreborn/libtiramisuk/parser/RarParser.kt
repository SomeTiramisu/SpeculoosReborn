package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.net.Uri
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator


class RarParser(private val resolver: ContentResolver, override val uri: Uri) : Parser {
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

    override fun at(index: Int): ByteArray {
        val rar = Archive(getInputStream())
        var e: FileHeader? = null
        for (i in 0..headers[index].index) {
            e = rar.nextFileHeader()
        }
        val iStream = rar.getInputStream(e)
        val r = iStream.readBytes()
        iStream.close()
        rar.close()
        return r
    }

    private fun getInputStream() = resolver.openInputStream(uri)

    companion object {
        fun isSupported(uri: Uri) =
            uri.lastPathSegment?.lowercase()?.contains(".*\\.(rar|cbr)$") ?: false
    }
}