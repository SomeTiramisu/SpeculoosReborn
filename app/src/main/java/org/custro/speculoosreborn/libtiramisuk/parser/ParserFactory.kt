package org.custro.speculoosreborn.libtiramisuk.parser

import org.custro.speculoosreborn.libtiramisuk.parser.ZipParser
import org.custro.speculoosreborn.libtiramisuk.parser.RarParser
import java.io.File

class ParserFactory(private val file: File) {
    companion object {
        fun create(file: File): Parser? {
            if (ZipParser.isSupported(file)) return ZipParser(file)
            if (RarParser.isSupported(file)) return RarParser(file)
            return null
        }
    }
}