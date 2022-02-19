package org.custro.speculoosreborn.parser

import java.io.Closeable

interface Parser: Closeable, AutoCloseable {
    val size: Int
    fun at(index: Int): ByteArray
    fun atRange(vararg indexes: Int): List<ByteArray>
}

data class Header(val index: Int, val filename: String)