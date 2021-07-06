package org.custro.speculoosreborn.libtiramisuk.parser

import android.net.Uri

interface Parser {
    val uri: Uri
    val size: Int
    fun at(index: Int): Uri
}

data class Header(val index: Int, val filename: String)