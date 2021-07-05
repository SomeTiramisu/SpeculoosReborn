package org.custro.speculoosreborn.libtiramisuk.utils

import org.custro.speculoosreborn.libtiramisuk.parser.Parser
import org.opencv.core.Mat
import org.opencv.core.Rect

data class PageRequest(val index: Int = -1, val width: Int = -1, val height: Int = -1, val parser: Parser? = null)

data class PagePair(val img: Mat = Mat(), val req: PageRequest = PageRequest())

data class PngPair(val png: ByteArray, val rec: Rect, val isBlack: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PngPair

        if (!png.contentEquals(other.png)) return false
        if (rec != other.rec) return false
        if (isBlack != other.isBlack) return false

        return true
    }

    override fun hashCode(): Int {
        var result = png.contentHashCode()
        result = 31 * result + rec.hashCode()
        result = 31 * result + isBlack.hashCode()
        return result
    }
}