package org.custro.speculoosreborn

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.room.AppDatabase
import org.custro.speculoosreborn.room.Manga

class InitModel: ViewModel() {
    private val _archiveUri = MutableLiveData(Uri.EMPTY)

    private val db =
        Room.databaseBuilder(App.instance.applicationContext, AppDatabase::class.java, "manga-database").build()

    fun insertManga(manga: Manga) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                db.mangaDao().insertAll(manga)
                Log.d("MainModel", "inserted: ${manga.uri}")
            } catch (e: SQLiteConstraintException) { //quite sure that should not be here
                Log.d("MainModel", "already inserted: ${manga.uri}")
            }
        }
    }

    fun getMangas(): LiveData<List<Manga>> {
        return db.mangaDao().getAll()
    }

    fun deleteManga(uri: String) {
        viewModelScope.launch(Dispatchers.Default) {
            db.mangaDao().deleteUri(uri)
            Log.d("MainModel", "deleted: $uri")
        }
    }
}
