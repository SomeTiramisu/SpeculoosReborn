package org.custro.speculoosreborn.parser

import java.io.File
import java.io.InputStream

class ParserFactory {
    companion object {
        fun create(getInputStream: () -> InputStream): Parser {
            if (ZipStreamParser.isSupported(getInputStream)) return ZipStreamParser(getInputStream)
            if (RarStreamParser.isSupported(getInputStream)) return RarStreamParser(getInputStream)
            throw Exception("Unsupported stream")
        }

        fun create(file: File): Parser {
            if (ZipFileParser.isSupported(file)) return ZipFileParser(file)
            if (RarFileParser.isSupported(file)) return RarFileParser(file)
            if (PdfFileParser.isSupported(file)) return PdfFileParser(file)
            throw Exception("Unsupported file")
        }

        fun isSupported(file: File): Boolean {
            return ZipFileParser.isSupported(file) or
                    RarFileParser.isSupported(file) or
                    PdfFileParser.isSupported(file)
        }

        fun isSupported(getInputStream: () -> InputStream): Boolean {
            return ZipStreamParser.isSupported(getInputStream) or
                    RarStreamParser.isSupported(getInputStream)
        }
    }
}