package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri

class ParserFactory() {
    companion object {
        fun create(uri: Uri): Parser {
            if (ZipFileParser.isSupported(uri)) return ZipFileParser(uri)
            if (ZipStreamParser.isSupported(uri)) return ZipStreamParser(uri)
            if (RarFileParser.isSupported(uri)) return RarFileParser(uri)
            if (RarStreamParser.isSupported(uri)) return RarStreamParser(uri)
            if (PdfFileParser.isSupported(uri)) return PdfFileParser(uri)
            throw Exception("Unsupported file")
        }
        fun isSupported(uri: Uri): Boolean {
            return ZipFileParser.isSupported(uri) or
                    ZipStreamParser.isSupported(uri) or
                    RarFileParser.isSupported(uri) or
                    RarStreamParser.isSupported(uri) or
                    PdfFileParser.isSupported(uri)
        }
    }
}