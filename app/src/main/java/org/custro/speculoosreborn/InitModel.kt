package org.custro.speculoosreborn

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache
import org.custro.speculoosreborn.room.AppDatabase
import org.custro.speculoosreborn.room.Manga
import org.custro.speculoosreborn.room.correctManga

class InitModel: ViewModel() {

    fun insertManga(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val manga: Manga
            MangaParser(uri).use {
                manga = genManga(it)
            }
            try {
                App.db.mangaDao().insertAll(manga)
                Log.d("MainModel", "inserted: ${manga.uri}")
            } catch (e: SQLiteConstraintException) { //quite sure that should not be here
                Log.d("MainModel", "already inserted: ${manga.uri}")
            }
        }
    }

    private fun genManga(parser: MangaParser): Manga {
        if (parser.uri.scheme == "file") {
            return Manga(parser.uri.toString(), parser.uri.toString(), parser.cover.toString())
        }
        return Manga(parser.uri.toString(), PageCache.saveData(parser.uri).toString(), parser.cover.toString())
    }

    fun getMangas(): LiveData<List<Manga>> {
        return App.db.mangaDao().getAll()
    }

    fun deleteManga(uri: String) {
        viewModelScope.launch(Dispatchers.Default) {
            App.db.mangaDao().deleteUri(uri)
            Log.d("MainModel", "deleted: $uri")
        }
    }
}
