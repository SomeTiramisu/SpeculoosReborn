package org.custro.speculoosreborn.libtiramisuk.utils

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

fun fromByteArray(src: ByteArray): Mat {
    if (src.isEmpty()) {
        return Mat()
    }
    val img = Imgcodecs.imdecode(Mat(1, src.size, CvType.CV_8UC1, ByteBuffer.wrap(src)), Imgcodecs.IMREAD_COLOR)
    Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGBA)
    return img
}