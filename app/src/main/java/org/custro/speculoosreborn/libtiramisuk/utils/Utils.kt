package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import org.opencv.core.Mat
import org.opencv.core.Rect

data class PngPair(val img: ByteArray, val rec: Rect, val isBlack: Boolean = false)
