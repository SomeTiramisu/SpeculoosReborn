package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri

class ParserFactory() {
    companion object {
        fun create(uri: Uri): Parser {
            if (ZipFileParser.isSupported(uri)) return ZipFileParser(uri)
            if (ZipStreamParser.isSupported(uri)) return ZipStreamParser(uri)
            if (RarFileParser.isSupported(uri)) return RarFileParser(uri)
            if (RarStreamParser.isSupported(uri)) return RarStreamParser(uri)
            throw Exception("Unsupported file")
        }
    }
}