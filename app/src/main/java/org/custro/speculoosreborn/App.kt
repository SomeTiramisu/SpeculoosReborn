package org.custro.speculoosreborn

import android.app.Application

class App: Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        var instance: Application? = null
    }
}