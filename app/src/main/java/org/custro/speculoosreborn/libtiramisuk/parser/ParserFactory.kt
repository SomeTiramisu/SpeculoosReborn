package org.custro.speculoosreborn.libtiramisuk.parser

import java.io.File

class ParserFactory {
    companion object {
        fun create(file: File): Parser? {
            if (ZipParser.isSupported(file)) return ZipParser(file)
            if (RarParser.isSupported(file)) return RarParser(file)
            return null
        }
    }
}