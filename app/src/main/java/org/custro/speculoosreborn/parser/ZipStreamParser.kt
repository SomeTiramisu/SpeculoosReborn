package org.custro.speculoosreborn.parser

import org.custro.speculoosreborn.utils.AlphanumComparator
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class ZipStreamParser(private val getInputStream: () -> InputStream) : Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    private var currentIndex: Int = 0
    private var inStream = getInputStream()
    private var zipStream = ZipInputStream(inStream)


    init {
        //Log.d("ZipStreamParser", "scheme: ${uri.scheme}")
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
        resetStreams()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator()) { it.filename }
        headers.sortWith(entryNaturalOrder)
        //Log.d("ZipParser", "size: ${headers.size}")
    }

    override fun at(index: Int): ByteArray {
        //Log.d("ZipStreamParser", "requested: $index")
        val entryIndex = headers[index].index
        return dataAt(entryIndex)
    }

    override fun atRange(vararg indexes: Int): List<ByteArray> {
        val entryIndexes = indexes.map { headers[it].index }.sorted()
        return entryIndexes.map { dataAt(it) }
    }

    @Synchronized
    private fun dataAt(entryIndex: Int): ByteArray {
        //Log.d("ZipStreamParser", "in file: $entryIndex")
        //Log.d("ZipStreamParser", "current: $currentIndex")
        var effectiveIndex = entryIndex - currentIndex
        if (effectiveIndex < 0) {
            resetStreams()
            effectiveIndex = entryIndex
        }
        //Log.d("ZipStreamParser", "effective: $effectiveIndex")

        for (i in 0..effectiveIndex) {
            zipStream.nextEntry
            currentIndex += 1
        }
        val r = zipStream.readBytes()
        zipStream.closeEntry()
        return r
    }

    override fun close() {
        zipStream.close()
        inStream.close()
    }

    private fun resetStreams() {
        //Log.d("ZipStreamParser", "streams reset")
        close()
        inStream = getInputStream()
        zipStream = ZipInputStream(inStream)
        currentIndex = 0
    }

    companion object {
        fun isSupported(getInputStream: () -> InputStream): Boolean {
            val buf = ByteArray(4)
            getInputStream().use {
                it.read(buf)
            }
            val magic = ByteBuffer.wrap(buf).int
            return magic == 0x504B0304 || magic == 0x504B0506
        }
    }
}