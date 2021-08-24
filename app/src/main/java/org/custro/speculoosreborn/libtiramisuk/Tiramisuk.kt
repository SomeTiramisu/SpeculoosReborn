package org.custro.speculoosreborn.libtiramisuk

import android.net.Uri
import android.util.Log
import org.custro.speculoosreborn.libtiramisuk.utils.PagePair
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.core.Mat

class Tiramisuk {
    private var mScheduler = PageScheduler()

    suspend fun get(req: PageRequest): Mat {
        Log.d("Tiramisuk", "req: $req")
        if (req.uri != Uri.EMPTY) {
            return mScheduler.at(req).img
        }
        return Mat()
    }
}