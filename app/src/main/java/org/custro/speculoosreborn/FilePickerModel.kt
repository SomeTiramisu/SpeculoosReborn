package org.custro.speculoosreborn

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.custro.speculoosreborn.room.Manga
import org.custro.speculoosreborn.room.genManga
import java.io.File

class FilePickerModel: ViewModel() {

    val initialDir = App.instance.applicationContext.getExternalFilesDir(null)!!
        .parentFile!!
        .parentFile!!
        .parentFile!!
        .parentFile!!
    private val _currentDir = MutableLiveData<File>(initialDir)
    val currentDir: LiveData<File> = _currentDir

    fun onCurrentDirChange(value: File) {
        if(value.isFile) {
            insertManga(value.toUri())
        } else {
        _currentDir.value = value
        }
    }
    fun onParentDir() {
        _currentDir.value = _currentDir.value?.parentFile
    }

    private fun insertManga(uri: Uri) {
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



}