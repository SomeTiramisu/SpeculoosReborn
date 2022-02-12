package org.custro.speculoosreborn.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.room.CachedFileEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object CacheUtils {
    private val cacheDir = App.instance.externalCacheDir?: App.instance.cacheDir
    private val dao = App.db.cacheDao()

    init {
        for(cachedFile in dao.getAll().value?: emptyList()) {
            if(System.currentTimeMillis() - cachedFile.lastAccess > 2592000000L) {
                File(cachedFile.path).delete()
                dao.delete(cachedFile)
            }
        }
    }

    fun save(stream: InputStream): String {
        val uuid = genUUID()
        save(stream, uuid)
        return uuid
    }

    fun save(stream: InputStream, uuid: String) {
        val output = File(cacheDir, uuid)
        val outputStream = FileOutputStream(output)
        stream.copyTo(outputStream)
        outputStream.close()
        dao.insert(CachedFileEntity(uuid, output.path, System.currentTimeMillis(), ""))
    }

    fun get(uuid: String): LiveData<File> {
        return Transformations.map(dao.get(uuid)) {
            File(it.path)
        }
    }

    fun getNow(uuid: String): File? {
        val cachedFile = dao.getNow(uuid)
        if (cachedFile != null) {
            return File(cachedFile.path)
        }
        return null
    }

    private fun genUUID(): String {
        return UUID.randomUUID().toString()
    }


}