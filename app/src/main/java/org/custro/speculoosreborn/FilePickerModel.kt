package org.custro.speculoosreborn

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.renderer.RendererFactory
import org.custro.speculoosreborn.room.genManga
import java.io.File

class FilePickerModel : ViewModel() {

    val initialDir: File

    val externalDirs: List<File>
    var currentExternalDirIndex = 0
    private val _currentExternalDirName: MutableLiveData<String>
    val currentExternalDirName: LiveData<String>

    init {
        externalDirs = App.instance.applicationContext.getExternalFilesDirs(null).toList()
            .map { getAllFiles(it) }
        _currentExternalDirName = MutableLiveData(getExternalDirName())
        currentExternalDirName = _currentExternalDirName
        initialDir = externalDirs[0]
    }

    private fun getAllFiles(file: File): File {
        return file.parentFile!!.parentFile!!.parentFile!!.parentFile!!
    }


    private val _currentDir = MutableLiveData<File>(initialDir)
    val currentDir: LiveData<File> = _currentDir

    fun onCurrentDirChange(value: File) {
        if (value.isFile) {
            if (RendererFactory.isSupported(value.toUri())) {
                insertManga(value.toUri())
            }
        } else {
            _currentDir.value = value
        }
    }

    fun onParentDir() {
        val nextDir = _currentDir.value?.parentFile
        if (nextDir?.exists() == true && (nextDir.listFiles() ?: arrayOf()).isNotEmpty()) {
            _currentDir.value = nextDir!!
        }
    }

    fun onExternalDirChange() {
        currentExternalDirIndex = (currentExternalDirIndex + 1) % externalDirs.size
        _currentDir.value = externalDirs[currentExternalDirIndex]
        _currentExternalDirName.value = getExternalDirName()
    }

    private fun getExternalDirName(): String {
        if (currentExternalDirIndex == 0) {
            return "Internal"
        }
        if (externalDirs.size > 2) {
            return "SD Card ${currentExternalDirIndex-1}"
        } else {
            return "SD Card"
        }
    }

    private fun insertManga(uri: Uri) {
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