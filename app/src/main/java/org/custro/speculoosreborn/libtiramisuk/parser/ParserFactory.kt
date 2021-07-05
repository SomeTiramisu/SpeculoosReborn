package org.custro.speculoosreborn.libtiramisuk.parser

import android.content.ContentResolver
import android.net.Uri

class ParserFactory {
    companion object {
        fun create(resolver: ContentResolver, uri: Uri): Parser? {
            if (ZipParser.isSupported(uri)) return ZipParser(resolver, uri)
            if (RarParser.isSupported(uri)) return RarParser(resolver, uri)
            return null
        }
    }
}