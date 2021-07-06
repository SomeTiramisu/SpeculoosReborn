package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.net.Uri

class ParserFactory {
    companion object {
        var resolver: ContentResolver? = null
        fun create(uri: Uri): Parser? {
            if (resolver == null) return null
            if (ZipParser.isSupported(uri)) return ZipParser(resolver!!, uri)
            if (RarParser.isSupported(uri)) return RarParser(resolver!!, uri)
            return null
        }
    }
}