package org.custro.speculoosreborn.libtiramisuk.utils

import org.opencv.core.Mat
import org.opencv.core.Rect
import java.io.File

data class PageRequest(val index: Int = -1, val width: Int = -1, val height: Int = -1, val file: File? = null)

data class PagePair(val img: Mat = Mat(), val req: PageRequest = PageRequest())

data class PngPair(val png: ByteArray, val rec: Rect)