package org.custro.speculoosreborn.libtiramisuk.parser

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class ZipParser(private val file: File): Parser {
    private val headers: MutableList<ZipEntry> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    init {
        val zip = ZipFile(file)
        for (e in zip.entries()) {
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(e)
            }
        }
        zip.close()
        val entryNaturalOrder = compareBy<ZipEntry>{it.name}
        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        val zip = ZipFile(file)
        val iStream = zip.getInputStream(headers[index])
        val r = iStream.readBytes()
        iStream.close()
        zip.close()
        return r
    }

    companion object {
        fun isSupported(file: File) = file.extension == "cbz"
    }
}