package org.custro.speculoosreborn.room

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.utils.PageCache
import org.custro.speculoosreborn.utils.bitmapToByteArray
import org.custro.speculoosreborn.utils.dpToPx


fun genManga(uri: Uri): Manga {

    if (uri.scheme == "file") {
        return Manga(uri.toString(), uri.toString(), genMangaCover(uri).toString())
    }
    return Manga(uri.toString(), PageCache.saveData(uri).toString(), genMangaCover(uri).toString())

    //return Manga(parser.uri.toString(), parser.uri.toString(), parser.cover.toString())
}

fun isMangaValid(manga: Manga): Boolean {
    return checkUri(manga.uri) && checkUri(manga.localUri) && checkUri(manga.cover)
}

fun checkUri(uri: String?): Boolean {
    return if (uri == null) {
       true
    } else {
        return Uri.parse(uri).let {
            if(it.scheme == "file") {
                it.toFile().exists()
            } else {
                true
            }
        }
    }
}

fun correctManga(manga: Manga) {
    if (isMangaValid(manga)) {
        return
    }
    var newManga: Manga = manga
    val dao = App.db.mangaDao()
    if (!checkUri(manga.uri)) {
        dao.delete(manga)
        return
    }
    if(!checkUri(manga.localUri)) {
        newManga = Manga(newManga.uri, PageCache.saveData(Uri.parse(manga.uri)).toString(), newManga.cover)
    }
    if(!checkUri(manga.cover)) {
        newManga = Manga(newManga.uri, newManga.localUri, genMangaCover(Uri.parse(manga.uri)).toString())
    }
    dao.updateManga(newManga)
    Log.d("correctManga", "${manga.uri} corrected")
}

fun genMangaCover(uri: Uri): Uri {
    val r: Uri
    val context = App.instance.applicationContext
    RendererFactory.create(uri).use { renderer ->
        renderer.openPage(0).use { rendererPage ->
            val bitmap = Bitmap.createBitmap(context.dpToPx(100), context.dpToPx(100), Bitmap.Config.ARGB_8888)
            val config = RenderConfig(
                addBorders = false,
                doScale = true,
                doCrop = false,
                doMask = false
            )
            rendererPage.render(bitmap, config)
            r = PageCache.saveData(bitmapToByteArray(bitmap), ".png")
        }
    }
    return r
}