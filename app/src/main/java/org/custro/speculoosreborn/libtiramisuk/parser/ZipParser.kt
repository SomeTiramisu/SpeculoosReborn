package org.custro.speculoosreborn.libtiramisuk.parser

import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipFile.OPEN_READ


class ZipParser(private val file: File): Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    init {
        val zip = ZipFile(file, OPEN_READ)
        var count = 0
        for (e in zip.entries()) {
            val name = e.name
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(Header(count, e.name))
            }
            count += 1
        }
        zip.close()
        //val entryNaturalOrder = compareBy<Header>{it.filename}
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), {it.filename})
        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        val zip = ZipFile(file, OPEN_READ)
        val iStream = zip.getInputStream(zip.getEntry(headers[index].filename))
        val r = iStream.readBytes()
        iStream.close()
        zip.close()
        return r
    }

    companion object {
        fun isSupported(file: File) = file.extension == "cbz"
    }
}