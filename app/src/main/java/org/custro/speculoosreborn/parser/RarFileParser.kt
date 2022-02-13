package org.custro.speculoosreborn.parser

import android.net.Uri
import androidx.core.net.toFile
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.utils.AlphanumComparator
import java.nio.ByteBuffer


class RarFileParser(override val uri: Uri) : Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }

    init {
        val rarFile = Archive(uri.toFile())
        var e: FileHeader? = rarFile.nextFileHeader()
        var count = 0
        while (e != null) {
            val name = e.fileName
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name))) {
                headers.add(Header(count, e.fileName))
            }
            e = rarFile.nextFileHeader()
            count += 1
        }
        rarFile.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })

        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        val entryIndex = headers[index].index
        return dataAt(entryIndex)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        TODO("Not yet implemented")
    }

    @Synchronized
    private fun dataAt(entryIndex: Int): ByteArray {
        val rarFile = Archive(uri.toFile())
        lateinit var e: FileHeader
        for (i in 0..entryIndex) {
            e = rarFile.nextFileHeader()
        }
        val entryInStream = rarFile.getInputStream(e)
        val r = entryInStream.readBytes()
        entryInStream.close()
        rarFile.close()
        return r
    }

    override fun close() {
    }

    companion object {
        //fun isSupported(uri: Uri) = uri.scheme == "file"
        //        && uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(rar|cbr)$")) ?: false
        fun isSupported(uri: Uri): Boolean {
            if (uri.scheme != "file") {
                return false
            }
            val buf = ByteArray(8)
            App.instance.contentResolver.openInputStream(uri)?.use {
                it.read(buf, 0, 7)
            }
            val magic = ByteBuffer.wrap(buf).long
            return magic == 0x526172211A0700
        }
    }
}