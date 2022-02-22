package org.custro.speculoosreborn.utils

import android.graphics.Bitmap
import android.net.Uri
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.room.MangaEntity
import org.opencv.android.Utils
import org.opencv.core.Mat

object MangaUtils {

    private val dao = App.db.mangaDao()

    suspend fun correctManga(entity: MangaEntity) {
        var newEntity = entity
        if (!CacheUtils.get(entity.coverId).exists()) {
            newEntity = genMangaEntity(Uri.parse(entity.uri))
        }
        dao.update(newEntity)
    }

    fun genMangaEntity(uri: Uri): MangaEntity {
        val coverId: String
        val pageCount: Int
        RendererFactory.create { App.instance.contentResolver.openInputStream(uri)!! }
            .use { renderer ->
                pageCount = renderer.pageCount
                renderer.openPage(0).use { rendererPage ->
                    val config = RenderConfig(
                        addBorders = false,
                        doScale = true,
                        doCrop = false,
                        doMask = false
                    )

                    val img = Mat()
                    rendererPage.render(img, 256, 256, config)
                    //bitmap.reconfigure(img.width(), img.height(), Bitmap.Config.ARGB_8888)
                    val bitmap = Bitmap.createBitmap(
                        img.cols(),
                        img.rows(),
                        Bitmap.Config.ARGB_8888
                    )
                    Utils.matToBitmap(img, bitmap)

                    bitmapToByteArray(bitmap).inputStream().use {
                        coverId = CacheUtils.save(it)
                    }
                }
            }
        return MangaEntity(uri.toString(), coverId, pageCount)
    }
}