package org.custro.speculoosreborn.libtiramisuk.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Core.merge
import org.opencv.core.Core.split
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc.*
import java.io.ByteArrayOutputStream
import kotlin.math.min

// A line will be considered as having content if 0.25% of it is filled.
const val FILLEDRATIOLIMIT = 0.0025

// When the threshold is closer to 1, less content will be cropped.
const val THRESHOLD = 0.75

fun fromByteArray(src: ByteArray): Mat {
    if (src.isEmpty()) {
        return Mat()
    }
    val img = Imgcodecs.imdecode(MatOfByte(*src), Imgcodecs.IMREAD_COLOR)
    cvtColor(img, img, COLOR_BGR2RGBA)
    return img
}

fun toByteArray(src: Mat): ByteArray {
    if (src.empty()) {
        return ByteArray(0)
    }
    val srcBGRA = Mat()
    cvtColor(src, srcBGRA, COLOR_RGBA2BGRA)
    val img = MatOfByte()
    Imgcodecs.imencode(".png", srcBGRA, img)
    return img.toArray()
}

fun matToBitmap(src: Mat): Bitmap {
    val bitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(src, bitmap)
    return bitmap
}

fun bitmapToMat(src: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(src, mat)
    return mat
}

fun bitmapToByteArray(src: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    src.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val res = stream.toByteArray()
    stream.close()
    return res
}

fun createMask(src: Mat, dst: Mat, notInv: Boolean = false) {
    cvtColor(src, dst, COLOR_RGBA2GRAY)
    threshold(dst, dst, 242.35, 255.0,
        if (notInv) THRESH_BINARY else THRESH_BINARY_INV)
}

fun scale(src: Mat, dst: Mat, width: Int, height: Int) {
    val imgWidth = src.cols()
    val imgHeight = src.rows()
    var fx = 1.0
    var fy = 1.0

    if (width==0 && height>0) {
        fy = height.toDouble()/imgHeight.toDouble()
        fx = fy
    }
    if (height==0 && width>0) {
        fx = width.toDouble()/imgWidth.toDouble()
        fy = fx
    }
    if (width>0 && height>0) {
        fx = width.toDouble()/imgWidth.toDouble()
        fy = height.toDouble()/imgHeight.toDouble()
    }
    val f = min(fx, fy)
    if (f > 1) {
        resize(src, dst, Size(), f, f, INTER_CUBIC)
    } else {
        resize(src, dst, Size(), f, f, INTER_AREA)
    }
}
/*
fun cropDetect(src: Mat): Rect {
    val mask1 = Mat()
    val mask2 = Mat()
    //createMask(src, mask)
    //TODO
    createMask(src, mask1)
    createMask(src, mask2, true)
    val rec1 = boundingRect(mask1)
    val rec2 = boundingRect(mask2)
    val rec3 = Rect()

    rec3.x = if (rec1.x>rec2.x) rec1.x else rec2.x
    rec3.y = if (rec1.y>rec2.y) rec1.x else rec2.x
    rec3.width = if (rec1.width>rec2.width) rec2.width else rec1.width
    rec3.height = if (rec1.height>rec2.height) rec2.height else rec1.height

    return rec3
}
*/
fun cropDetect(src: Mat): Rect {
    val maskWhite = Mat()
    val maskBlack = Mat()
    createMask(src, maskWhite)
    createMask(src, maskBlack, true)
    val recWhite = boundingRect(maskWhite)
    val recBlack = boundingRect(maskBlack)

    return if (recWhite.area()>recBlack.area()) recBlack else recWhite
}

fun blackDetect(src: Mat): Boolean {
    val mask = Mat()
    createMask(src, mask)
    val total = (src.cols()+src.rows())*2
    var blacks = 0
    for (i in 0 until mask.cols()) {
        //Log.d("ImageProc", "coucou: ${mask.get(0, i).contentToString()}")
        if (mask.get(0, i).contentEquals(doubleArrayOf(255.0))) {
            blacks += 1
        }
        if (mask.get(mask.rows()-1, i).contentEquals(doubleArrayOf(255.0))) {
            blacks += 1
        }
    }
    for (i in 0 until mask.rows()) {
        if (mask.get(i, 0).contentEquals(doubleArrayOf(255.0))) {
            blacks += 1
        }
        if (mask.get(i, src.cols()-1).contentEquals(doubleArrayOf(255.0))) {
            blacks += 1
        }
    }
    //Log.d("ImageProc", "blacks: $blacks")
    return blacks.toDouble() > 0.5*total.toDouble()
}

fun cropScaleMaskProcess(src: Mat, dst: Mat, roi: Rect?, width: Int, height: Int) {
    val tmp = Mat()
    val mask = Mat()
    if (roi == null || roi.empty()) {
        scale(src, tmp, width, height)
    } else {
        scale(src.submat(roi), tmp, width, height)
    }
    createMask(tmp, mask)
    tmp.copyTo(dst, mask) //do not work if tmp.size == dst.size, only work when dst needs to be reconfigured
}

fun cropProcessNew(src: Mat, dst: Mat, roi: Rect) {
    if (roi.empty()) {
        src.copyTo(dst)
    } else {
        src.submat(roi).copyTo(dst)
    }
}

fun scaleProcessNew(src: Mat, dst: Mat, width: Int, height: Int) {
    scale(src, dst, width, height)
}

fun maskProcessNew(src: Mat, dst: Mat) {
    val tmp = Mat()
    val mask = Mat()
    createMask(src, mask)
    src.copyTo(tmp, mask)
    tmp.copyTo(dst)
}

fun addBlackBorders(src: Mat, dst: Mat, width: Int, height: Int) {
    val tmp = Mat(height, width, src.type(), Scalar(0.0, 0.0, 0.0, 255.0))
    val xOffset = (width-src.cols())/2
    val yOffset = (height-src.rows())/2
    //Log.d("ImageProc", "$xOffset, $yOffset")
    val subRec = Rect(xOffset, yOffset,src.cols(), src.rows())
    //val subMat = Mat(tmp, Rect(0, 0, src.cols(), src.rows()))
    val subMat = Mat(tmp, subRec)
    src.copyTo(subMat)
    tmp.copyTo(dst)
    //src.copyTo(dst)
}

fun RGBA2ARGB(src: Mat, dst: Mat) {
    val srcChannels = mutableListOf<Mat>()
    split(src, srcChannels)
    val dstChannels = listOf(srcChannels[3], srcChannels[0], srcChannels[1], srcChannels[2])
    merge(dstChannels, dst)
}