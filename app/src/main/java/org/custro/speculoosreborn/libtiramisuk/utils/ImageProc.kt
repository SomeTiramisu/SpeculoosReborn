package org.custro.speculoosreborn.libtiramisuk.utils

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

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