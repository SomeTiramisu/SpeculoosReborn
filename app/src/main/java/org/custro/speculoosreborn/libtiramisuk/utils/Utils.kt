package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import org.opencv.core.Mat
import org.opencv.core.Rect

data class PageRequest(val index: Int = -1, val width: Int = -1, val height: Int = -1, val uri: Uri = Uri.EMPTY)

data class PagePair(val img: Mat = Mat(), val req: PageRequest = PageRequest())

data class PngPair(val img: ByteArray, val rec: Rect, val isBlack: Boolean = false)