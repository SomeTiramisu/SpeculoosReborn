package org.custro.speculoosreborn.libtiramisuk.parser

interface Parser {
    val size: Int
    fun at(index: Int): ByteArray
}