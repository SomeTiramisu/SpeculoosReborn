package org.custro.speculoosreborn.parser

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.utils.AlphanumComparator
import java.nio.ByteBuffer
import java.util.zip.ZipFile
import java.util.zip.ZipFile.OPEN_READ


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

    override fun at(index: Int): ByteArray {
        Log.d("ZipFileParser", "Reading $index")
        val entryName = headers[index].filename
        return dataAt(entryName)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        TODO("Not yet implemented")
    }

    @Synchronized
    private fun dataAt(entryName: String): ByteArray {
        val zipFile = ZipFile(uri.toFile(), OPEN_READ)
        val entryInStream = zipFile.getInputStream(zipFile.getEntry(entryName))
        val r = entryInStream.readBytes()
        entryInStream.close()
        zipFile.close()
        return r
    }

    override fun close() {
    }

    companion object {
        //fun isSupported(uri: Uri) = uri.scheme == "file"
        //       && uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(zip|cbz)$")) ?: false
        fun isSupported(uri: Uri): Boolean {
            if (uri.scheme != "file") {
                return false
            }
            val buf = ByteArray(4)
            App.instance.contentResolver.openInputStream(uri)!!.read(buf)
            val magic = ByteBuffer.wrap(buf).int
            return magic == 0x504B0304 || magic == 0x504B0506
        }
    }
}