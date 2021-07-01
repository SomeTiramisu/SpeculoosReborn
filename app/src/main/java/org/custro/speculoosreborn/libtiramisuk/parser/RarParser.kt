package org.custro.speculoosreborn.libtiramisuk.parser

import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import org.custro.speculoosreborn.libtiramisuk.utils.AlphanumComparator
import java.io.File


class RarParser(private val file: File): Parser {
    private val headers: MutableList<Header> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    init {
        val rar = Archive(file)
        var e: FileHeader? = rar.nextFileHeader()
        var count = 0
        while (e != null){
            val name = e.fileName
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(Header(count, e.fileName))
            }
            e = rar.nextFileHeader()
            count += 1
        }
        rar.close()
        val entryNaturalOrder = compareBy<Header, String>(AlphanumComparator(), {it.filename})

        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        val rar = Archive(file)
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

    companion object {
        fun isSupported(file: File) = file.extension == "cbr"
    }
}