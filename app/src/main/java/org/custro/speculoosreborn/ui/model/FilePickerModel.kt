package org.custro.speculoosreborn.ui.model

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.room.genManga
import java.io.File

class FilePickerModel : ViewModel() {
    val externalDirs: List<File>
    //TODO: crash if no externaldir
    private val _currentExternalDirIndex = MutableLiveData(0)
    val currentExternalDirIndex: LiveData<Int> = _currentExternalDirIndex

    init {
        externalDirs = App.instance.applicationContext.getExternalFilesDirs(null).toList()
            .map { getBaseDir(it) }
    }

    private fun getBaseDir(file: File): File {
        return file.parentFile!!.parentFile!!.parentFile!!.parentFile!!
    }

    fun onExternalDirChange(index: Int) {
        _currentExternalDirIndex.value =  index % externalDirs.size
    }

    fun getExternalDirName(index: Int): String {
        return when(index) {
            0 -> "Internal"
            else -> if (externalDirs.size > 2) {
                "SD Card ${index-1}"
            } else {
                "SD Card"
            }
        }
    }

    fun insertManga(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val manga = genManga(uri)
            try {
                App.db.mangaDao().insertAll(manga)
                Log.d("MainModel", "inserted: ${manga.uri}")
            } catch (e: SQLiteConstraintException) { //quite sure that should not be here
                Log.d("MainModel", "already inserted: ${manga.uri}")
            }
        }
    }
}