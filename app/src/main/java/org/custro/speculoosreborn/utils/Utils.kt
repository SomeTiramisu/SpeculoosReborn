package org.custro.speculoosreborn.utils

import android.content.Context
import android.graphics.Bitmap
import org.opencv.core.Mat
import org.opencv.core.Rect

typealias DetectTriple = Triple<ByteArray, Rect, Boolean>
typealias CropPair = Pair<Mat, Boolean>

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.pxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).toInt()
}

fun emptyBitmap(): Bitmap {
    return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}