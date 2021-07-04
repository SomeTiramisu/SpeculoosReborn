package org.custro.speculoosreborn.libtiramisuk

import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mPreloaderProgressSlot: (Int) -> Unit = {}
    private var imageCallback: (Mat) -> Unit = {}
    private var sizeCallback: (Int) -> Unit = {}

    init {
        mScheduler.connectPageCallback {res: PagePair ->
            if (mReq == res.req) {
                imageCallback(res.img)
            }
        }
        mScheduler.connectSizeCallback { sizeCallback }
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
    fun connectSizeCallback(slot: (Int) -> Unit) {
        sizeCallback = slot
    }
    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

}