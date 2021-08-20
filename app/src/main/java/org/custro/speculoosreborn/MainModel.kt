package org.custro.speculoosreborn

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

class MainModel: ViewModel() {
    private val _archiveUri = MutableLiveData(Uri.EMPTY)
    val archiveUri: LiveData<Uri> = _archiveUri

    private val _openReadNow: MutableLiveData<Boolean?> = MutableLiveData(false)
    val openReadNow: LiveData<Boolean?> = _openReadNow

    private val db =
        Room.databaseBuilder(App.instance!!.applicationContext, AppDatabase::class.java, "manga-database").build()

    fun insertManga(manga: Manga) {
        viewModelScope.launch(Dispatchers.Default) {
            db.mangaDao().insertAll(manga)
            Log.d("MainModel", "inserted: ${manga.uri.toString()}")
        }
    }

    fun getMangas(): LiveData<List<Manga>> {
        return db.mangaDao().getAll()
    }

    fun onArchiveUriChange(value: Uri) {
        _archiveUri.value = value
        //insertManga(Manga(value.toString()))
    }
}
