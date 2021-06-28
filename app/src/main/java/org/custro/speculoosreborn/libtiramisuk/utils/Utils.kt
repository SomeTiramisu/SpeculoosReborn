package org.custro.speculoosreborn.libtiramisuk.utils

import java.io.File

data class PageRequest(val index: Int = -1, val width: Int = -1, val height: Int = -1, val file: File)

data class PagePair(val img: ByteArray, val req: PageRequest)

data class PngPair(val png: ByteArray)