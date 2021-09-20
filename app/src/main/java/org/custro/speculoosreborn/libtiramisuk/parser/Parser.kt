package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri
import java.io.Closeable

interface Parser: Closeable, AutoCloseable {
    val uri: Uri
    val size: Int
    fun at(index: Int): ByteArray
}

data class Header(val index: Int, val filename: String)