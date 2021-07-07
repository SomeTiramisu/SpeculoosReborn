package org.custro.speculoosreborn.libtiramisuk.utils

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.custro.speculoosreborn.App
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PageCache() {
    companion object {
        private val cacheDir =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) App.instance!!.externalCacheDir else App.instance!!.cacheDir

        fun saveData(data: ByteArray): Uri { // return a file: scheme uri
            val output = File(cacheDir, data.hashCode().toString())
            output.writeBytes(data)
            return output.toUri()
        }

        fun saveData(uri: Uri): Uri { // return a file: scheme uri
            val output = File(cacheDir, uri.hashCode().toString()+"."+findExt(uri))
            val inputStream = App.instance!!.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            inputStream!!.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            //Log.d("ZipParser", output.toUri().toString())
            return output.toUri()
        }

        fun saveData(stream: InputStream): Uri { // return a file: scheme uri
            val output = File(cacheDir, stream.hashCode().toString())
            val outputStream = FileOutputStream(output)
            stream.copyTo(outputStream)
            Log.d("Cache", "$DEFAULT_BUFFER_SIZE")
            outputStream.close()
            return output.toUri()
        }

        fun loadData(uri: Uri): ByteArray {
            val input = uri.toFile()
            return input.readBytes()
        }

        private fun findExt(uri: Uri): String {
            return uri.lastPathSegment?.substringAfterLast('.', "") ?: ""
        }
    }
}