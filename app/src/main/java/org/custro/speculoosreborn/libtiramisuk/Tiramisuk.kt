package org.custro.speculoosreborn.libtiramisuk

import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()
    private var mReq = PageRequest()
    private var mPreloaderProgressSlot: (Int) -> Unit = {Log.d("Tiramisu", "empty ProgressSlot")}
    private var imageCallback: (Mat) -> Unit = {Log.d("Tiramisu", "empty ImageSlot")}

    fun get(req: PageRequest) {
        if (req.parser != null) {
            mReq = req
            mScheduler.at(req)
        }
    }

    fun connectImageCallback(slot: (Mat) -> Unit) {
        imageCallback = slot
        mScheduler.connectPageCallback {res: PagePair ->
            Log.d("Scheduler", "PageCallback")
            if (mReq == res.req) {
                Log.d("Scheduler", "req hceck passed")
                imageCallback(res.img)
            }
        }
    }

    fun connectPreloaderProgress(slot: (Int) -> Unit) {
        mPreloaderProgressSlot = slot
    }

}