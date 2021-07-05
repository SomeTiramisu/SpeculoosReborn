package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mPreloaderProgressSlot: (Int) -> Unit = {}
    private var imageCallback: (Mat) -> Unit = {}
    private var maxIndexCallback: (Int) -> Unit = {}

    init {
        mScheduler.connectPageCallback {res: PagePair ->
            if (mReq == res.req) {
                imageCallback(res.img)
            }
        }
    }

    fun get(req: PageRequest) {
        if (req.file != null) {
            mReq = req
            mScheduler.at(req)
        }
    }

    fun connectImageCallback(slot: (Mat) -> Unit) {
        imageCallback = slot
    }
    fun connectMaxIndexCallback(slot: (Int) -> Unit) {
        maxIndexCallback = slot
        mScheduler.connectSizeCallback(maxIndexCallback) //we send current book size here
    }
    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

}