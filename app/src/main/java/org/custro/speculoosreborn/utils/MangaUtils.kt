package org.custro.speculoosreborn.utils

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.room.MangaEntity

object MangaUtils {

    private val dao = App.db.mangaDao()

    fun genMangaEntity(uri: Uri): MangaEntity {
        return MangaEntity(uri.toString(), genMangaCover(uri))
    }

    fun correctManga(entity: MangaEntity) {
            var newEntity = entity
            val cachedCover = CacheUtils.getNow(entity.coverId)
            if (cachedCover == null || !cachedCover.exists()) {
                newEntity = MangaEntity(entity.uri, genMangaCover(Uri.parse(entity.uri)))
            }
            dao.update(entity)
    }

    private fun genMangaCover(uri: Uri): String {
        val r: String
        RendererFactory.create(uri).use { renderer ->
            renderer.openPage(0).use { rendererPage ->
                val bitmap = Bitmap.createBitmap(
                    256,
                    256,
                    Bitmap.Config.ARGB_8888
                )
                val config = RenderConfig(
                    addBorders = false,
                    doScale = true,
                    doCrop = false,
                    doMask = false
                )
                rendererPage.render(bitmap, config)
                bitmapToByteArray(bitmap).inputStream().use {
                    r = CacheUtils.save(it)
                }
            }
        }
        return r
    }
}