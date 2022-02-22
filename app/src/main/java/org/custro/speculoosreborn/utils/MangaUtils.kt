package org.custro.speculoosreborn.utils

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.room.MangaEntity
import org.opencv.android.Utils
import org.opencv.core.Mat

object MangaUtils {

    private val dao = App.db.mangaDao()

    fun genMangaEntity(uri: Uri): MangaEntity {
        return MangaEntity(uri.toString(), genMangaCover(uri))
    }

    suspend fun correctManga(entity: MangaEntity) {
            var newEntity = entity
            if (!CacheUtils.get(entity.coverId).exists()) {
                newEntity = MangaEntity(entity.uri, genMangaCover(Uri.parse(entity.uri)))
            }
            dao.update(newEntity)
    }

    private fun genMangaCover(uri: Uri): String {
        val r: String
        //TODO: enable for all uri type
        RendererFactory.create(uri.toFile()).use { renderer ->
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

                val img = Mat()
                rendererPage.render(img, 256, 256, config)
                //bitmap.reconfigure(img.width(), img.height(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(img, bitmap)

                bitmapToByteArray(bitmap).inputStream().use {
                    r = CacheUtils.save(it)
                }
            }
        }
        return r
    }
}