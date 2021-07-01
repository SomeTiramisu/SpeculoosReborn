package org.custro.speculoosreborn.libtiramisuk.parser

import com.github.junrar.Archive
import com.github.junrar.rarfile.FileHeader
import java.io.File


class RarParser(private val file: File): Parser {
    private val headers: MutableList<FileHeader> = mutableListOf()
    override val size: Int
        get() {
            return headers.size
        }
    init {
        val rar = Archive(file)
        val e: FileHeader? = rar.nextFileHeader()
        while (e != null){
            val name = e.fileName
            if (!e.isDirectory and ((".jpg" in name) or (".png" in name ))) {
                headers.add(e)
            }
        }
        rar.close()
        val entryNaturalOrder = compareBy<FileHeader>{it.fileName}
        headers.sortWith(entryNaturalOrder)
    }

    override fun at(index: Int): ByteArray {
        val zip = Archive(file)
        val iStream = zip.getInputStream(headers[index])
        val r = iStream.readBytes()
        iStream.close()
        zip.close()
        return r
    }

    companion object {
        fun isSupported(file: File) = file.extension == "cbr"
    }
}