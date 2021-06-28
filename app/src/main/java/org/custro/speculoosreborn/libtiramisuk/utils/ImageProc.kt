package org.custro.speculoosreborn.libtiramisuk.utils

import android.os.strictmode.ImplicitDirectBootViolation
import org.opencv.core.*
import org.opencv.core.Core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import kotlin.math.min

fun fromByteArray(src: ByteArray): Mat {
    if (src.isEmpty()) {
        return Mat()
    }
    val buf = ByteBuffer.wrap(src)
    //val img = Imgcodecs.imdecode(Mat(1, src.size, CvType.CV_8UC1, buf), Imgcodecs.IMREAD_COLOR)
    val img = Imgcodecs.imdecode(MatOfByte(*src), Imgcodecs.IMREAD_COLOR)
    //val img = Imgcodecs.imread("/storage/emulated/0/000.jpg")
    Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGBA)
    return img
}

fun toPng(src: Mat): ByteArray {
    TODO()
}

fun createMask(src: Mat, dst: Mat) {
    Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGBA2GRAY)
    Imgproc.threshold(dst, dst, 240.0, 255.0, Imgproc.THRESH_BINARY_INV)
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
        Imgproc.resize(src, dst, Size(), f, f, Imgproc.INTER_CUBIC)
    } else {
        Imgproc.resize(src, dst, Size(), f, f, Imgproc.INTER_AREA)
    }
}

fun cropDetect(src: Mat): Rect {
    val mask = Mat()
    createMask(src, mask)
    //TODO
    return Rect()
}
fun cropScaleProcess(src: Mat, dst: Mat, roi: Rect, width: Int, height: Int) {
    val tmp = Mat()
    val mask = Mat()
    var froi = if (roi.empty()) Rect(0, 0, src.cols(), src.rows()) else roi
    scale(src, tmp, width, height)
    createMask(tmp, mask)
    tmp.copyTo(dst, mask)
}

fun RGBA2ARGB(src: Mat, dst: Mat) {
    val srcChannels = mutableListOf<Mat>()
    split(src, srcChannels)
    val dstChannels = listOf<Mat>(srcChannels[3], srcChannels[0], srcChannels[1], srcChannels[2])
    merge(dstChannels, dst)
}