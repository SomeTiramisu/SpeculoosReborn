package org.custro.speculoosreborn.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.room.CachedFileEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object CacheUtils {
    private val cacheDir = App.instance.externalCacheDir ?: App.instance.cacheDir
    private val dao = App.db.cacheDao()

    init {
        //TODO: clean old files + update lastAccess on read
        runBlocking {/*
            dao.getAll().collect { cachedFiles ->
                for(cachedFile in cachedFiles) {
                    if(System.currentTimeMillis() - cachedFile.lastAccess > 2592000000L) {
                        File(cachedFile.path).delete()
                        dao.delete(cachedFile)
                    }
                }
            }*/
        }
    }

    fun save(stream: InputStream): String {
        val uuid = genUUID()
        save(stream, uuid)
        return uuid
    }

    fun save(stream: InputStream, uuid: String) {
        Log.d("CacheUtil", "saving $uuid")
        val output = File(cacheDir, uuid)
        val outputStream = FileOutputStream(output)
        stream.copyTo(outputStream)
        outputStream.close()
        dao.insert(CachedFileEntity(uuid, output.path, System.currentTimeMillis(), ""))
    }

    suspend fun get(uuid: String): File {
        Log.d("CacheUtil", "loading $uuid")
        return File(dao.get(uuid).path)
    }

    private fun genUUID(): String {
        return UUID.randomUUID().toString()
    }


}