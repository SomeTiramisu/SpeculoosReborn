package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import org.opencv.core.Mat
import org.opencv.core.Rect

typealias DetectTriple = Triple<ByteArray, Rect, Boolean>
typealias CropPair = Pair<Mat, Boolean>
