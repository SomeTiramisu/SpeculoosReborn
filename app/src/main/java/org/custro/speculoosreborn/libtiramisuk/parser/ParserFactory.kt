package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

class ParserFactory(private var context: Context) {
    companion object {
        fun create(uri: Uri): Parser? {
            if (ZipParser.isSupported(uri)) return ZipParser(uri)
            if (RarParser.isSupported(uri)) return RarParser(uri)
            return null
        }
    }
}