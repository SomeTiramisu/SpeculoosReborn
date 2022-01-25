package org.custro.speculoosreborn.utils

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.custro.speculoosreborn.App
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class PageCache() {
    companion object {
        private val cacheDir =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) App.instance.externalCacheDir else App.instance.cacheDir

        fun saveData(data: ByteArray, ext: String? = null): Uri { // return a file: scheme uri
            return saveData(data.inputStream(), ext)
        }

        fun saveData(uri: Uri): Uri { // return a file: scheme uri
            val output = File(cacheDir, concatExt(genUUID(), findExt(uri)))
            val inputStream = App.instance.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            inputStream!!.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            //Log.d("ZipParser", output.toUri().toString())
            return output.toUri()
        }

        fun saveData(stream: InputStream, ext: String? = null): Uri { // return a file: scheme uri
            val output = File(cacheDir, concatExt(genUUID(), ext))
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

        private fun genUUID(): String {
            return UUID.randomUUID().toString()
        }

        private fun normaliseExt(ext: String): String {
            if(ext.first()=='.') {
                return ext
            }
            return ".$ext"
        }

        private fun concatExt(name: String, ext: String?): String {
            return "$name${ext?.let { normaliseExt(it) }?: ""}"
        }

        fun delete() {
            cacheDir?.deleteRecursively()
        }
    }
}