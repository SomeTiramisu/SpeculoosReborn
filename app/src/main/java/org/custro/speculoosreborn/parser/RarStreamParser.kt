package org.custro.speculoosreborn.parser

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.utils.AlphanumComparator
import java.nio.ByteBuffer


class RarStreamParser(override val uri: Uri) : Parser {
    private val resolver = App.instance.contentResolver
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    private var currentIndex: Int = 0
    private var inStream = getInputStream()
    private var rarStream = Archive(inStream)

    init {
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
        resetStreams()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })
        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        Log.d("RarStreamParser", "requested: $index")
        val entryIndex = headers[index].index
        return dataAt(entryIndex)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        val entryIndexes = indexes.map { headers[it].index }.sorted()
        return entryIndexes.map { dataAt(it) }
    }

    @Synchronized
    private fun dataAt(entryIndex: Int): ByteArray {
        Log.d("RarStreamParser", "in file: $entryIndex")
        Log.d("RarStreamParser", "current: $currentIndex")
        var effectiveIndex = entryIndex - currentIndex
        if (effectiveIndex < 0) {
            resetStreams()
            effectiveIndex = entryIndex
        }
        Log.d("RarStreamParser", "effective: $effectiveIndex")

        lateinit var e: FileHeader
        for (i in 0..effectiveIndex) {
            e = rarStream.nextFileHeader()
            currentIndex += 1
        }
        val r: ByteArray
        rarStream.getInputStream(e).use {
            r = it.readBytes()
        }
        return r
    }


    override fun close() {
        rarStream.close()
        inStream.close()
    }

    private fun getInputStream() =
        if (uri.scheme == "file") uri.toFile().inputStream() else resolver.openInputStream(uri)!!

    private fun resetStreams() {
        close()
        inStream = getInputStream()
        rarStream = Archive(inStream)
        currentIndex = 0
    }

    companion object {
        //fun isSupported(uri: Uri) =
        //    uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(rar|cbr)$")) ?: false
        fun isSupported(uri: Uri): Boolean {
            val buf = ByteArray(8)
            App.instance.contentResolver.openInputStream(uri)!!.read(buf, 0, 7)
            val magic = ByteBuffer.wrap(buf).long
            return magic == 0x526172211A0700
        }
    }
}