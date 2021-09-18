package org.custro.speculoosreborn.room

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.libtiramisuk.utils.*

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
        val mangaParser = MangaParser(Uri.parse(manga.uri))
        newManga = Manga(newManga.uri, newManga.localUri, mangaParser.cover.toString())
    }
    dao.updateManga(newManga)
    Log.d("correctManga", "${manga.uri} corrected")
}