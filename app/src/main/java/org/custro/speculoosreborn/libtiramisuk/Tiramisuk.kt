package org.custro.speculoosreborn.libtiramisuk

import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mImageSlot: (Mat) -> Unit = {}
    private var mSizeSlot: (Int) -> Unit = {}
    private var mPreloaderProgressSlot: (Int) -> Unit = {}

    init {
        mScheduler.connectPageSlot {res: PagePair ->
            if (mReq == res.req) {
                mImageSlot(res.img)
            }
        }
        mScheduler.connectSizeSlot {res: Int ->
            mSizeSlot(res)
        }
    }

    fun init(imageCallback: (Mat) -> Unit, sizeCallback: (Int) -> Unit, restore: Boolean = false) {
        mSizeSlot = sizeCallback
        mImageSlot = imageCallback
        mScheduler.sendBookSize()
        if (restore) {
            get(mReq)
        }
    }

    fun get(req: PageRequest) {
        if (req.file == null) {
            mImageSlot(Mat())
            return
        }
        mReq = req
        mScheduler.at(req)
    }


    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

}