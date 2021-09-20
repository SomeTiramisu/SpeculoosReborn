package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.math.max


class ZipStreamParser(override val uri: Uri) : Parser {
    private val resolver = App.instance.contentResolver
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    private var currentIndex: Int = 0
    private var inStream = getInputStream()
    private var zipStream = ZipInputStream(inStream)


    init {
        Log.d("ZipStreamParser", "scheme: ${uri.scheme}")
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
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), { it.filename })
        headers.sortWith(entryNaturalOrder)
        //Log.d("ZipParser", "size: ${headers.size}")
    }

    @Synchronized
    override fun at(index: Int): ByteArray {
        //Log.d("ZipStreamParser", "requested: $index")
        val entryIndex = headers[index].index
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

    private fun getInputStream() =
        if (uri.scheme == "file") uri.toFile().inputStream() else resolver.openInputStream(uri)!!

    private fun resetStreams() {
        Log.d("ZipStreamParser", "streams reset")
        close()
        inStream = getInputStream()
        zipStream = ZipInputStream(inStream)
        currentIndex = 0
    }

    companion object {
        fun isSupported(uri: Uri) =
            uri.lastPathSegment?.lowercase()?.matches(Regex(".*\\.(zip|cbz)$")) ?: false
    }
}