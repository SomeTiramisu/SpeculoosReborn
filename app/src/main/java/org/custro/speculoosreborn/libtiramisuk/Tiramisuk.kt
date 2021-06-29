package org.custro.speculoosreborn.libtiramisuk

import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mSchedulerImageSlot: (Mat) -> Unit = {}
    private var mBookSizeSlot: (Int) -> Unit = {}
    private var mPreloaderProgressSlot: (Int) -> Unit = {}

    init {
        mScheduler.connectPageSlot {res: PagePair ->
            if (mReq == res.req) {
                mSchedulerImageSlot(res.img)
            }
        }
        mScheduler.connectSizeSlot {res: Int ->
            mBookSizeSlot(res)
        }
    }

    fun get(req: PageRequest) {
        if (req.file == null) {
            mSchedulerImageSlot(Mat())
            return
        }
        mReq = req
        mScheduler.at(req)
    }


    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

    fun connectBookSize(slot: (Int) -> Unit) {
        mBookSizeSlot = slot
        mScheduler.sendBookSize() //keep an eye on it for book changing
    }

    fun connectImage(slot: (Mat) -> Unit) {
        mSchedulerImageSlot = slot
    }
}