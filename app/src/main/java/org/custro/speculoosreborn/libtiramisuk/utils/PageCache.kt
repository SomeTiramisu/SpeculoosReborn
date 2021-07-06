package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.custro.speculoosreborn.App
import java.io.File

class PageCache() {
    companion object {
        private val cacheDir =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) App.instance!!.externalCacheDir else App.instance!!.cacheDir
        private var cacheId = 0
        fun saveData(data: ByteArray): Uri { // return a file: scheme uri
            val output = File(cacheDir, cacheId.toString())
            cacheId.inc()
            output.writeBytes(data)
            return output.toUri()
        }
        fun saveData(uri: Uri): Uri { // return a file: scheme uri
            val output = File(cacheDir, cacheId.toString())
            cacheId.inc()
            val bytes = App.instance!!.contentResolver.openInputStream(uri)!!.readBytes()
            output.writeBytes(bytes)
            return output.toUri()
        }
        fun loadData(uri: Uri): ByteArray {
            val input = uri.toFile()
            return input.readBytes()
        }
    }
}