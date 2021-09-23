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
import org.custro.speculoosreborn.room.genManga

class InitModel: ViewModel() {

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
