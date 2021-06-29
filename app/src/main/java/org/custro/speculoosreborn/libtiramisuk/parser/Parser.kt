package org.custro.speculoosreborn.libtiramisuk.parser

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class Parser(private val file: File) {
    private val headers: MutableList<ZipEntry> = mutableListOf()
    var size: Int = 0
    init {
        val zip = ZipFile(file)
        size = zip.size()
        for (e in zip.entries()) {
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(e)
            }
        }
        val entryNaturalOrder = compareBy<ZipEntry>{it.name}
        headers.sortWith(entryNaturalOrder)
    }

    fun at(index: Int): ByteArray {
        val zip = ZipFile(file)
        val iStream = zip.getInputStream(headers[index])
        val r = iStream.readBytes()
        iStream.close()
        return r
    }
}