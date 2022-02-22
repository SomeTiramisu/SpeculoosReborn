package org.custro.speculoosreborn

import android.app.Application
import androidx.room.Room
import org.custro.speculoosreborn.room.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        System.loadLibrary("opencv_java4")
        instance = this
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "manga-database")
            .build()
    }

    companion object {
        lateinit var instance: Application
        lateinit var db: AppDatabase
    }
}