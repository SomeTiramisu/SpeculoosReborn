package org.custro.speculoosreborn.libtiramisuk.parser

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class Parser(file: File) {
    private val headers: MutableList<ZipEntry> = mutableListOf()
    private val mZip: ZipFile = ZipFile(file.absolutePath)
    init {
        for (e in mZip.entries()) {
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(e)
            }
        }
        val entryNaturalOrder = compareBy<ZipEntry>{it.name}
        headers.sortWith(entryNaturalOrder)
    }

    fun at(index: Int): ByteArray {
        val iStream = mZip.getInputStream(headers[index])
        val r = iStream.readBytes()
        iStream.close()
        return r
    }

    val size: Int
        get() = mZip.size()

    fun destroy() {
        mZip.close()
    }


}