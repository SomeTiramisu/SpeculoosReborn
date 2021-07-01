package org.custro.speculoosreborn.libtiramisuk

import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mPreloaderProgressSlot: (Int) -> Unit = {}

    fun init(imageCallback: (Mat) -> Unit, sizeCallback: (Int) -> Unit) {
        val pageCallback = {res: PagePair ->
            if (mReq == res.req) {
                imageCallback(res.img)
            }
        }
        mScheduler.init(pageCallback, sizeCallback)
    }

    fun get(req: PageRequest) {
        if (req.file != null) {
            mReq = req
            mScheduler.at(req)
        }
    }

    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

}