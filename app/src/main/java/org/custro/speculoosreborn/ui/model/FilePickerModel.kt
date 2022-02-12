package org.custro.speculoosreborn.ui.model

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.App
import java.io.File

class FilePickerModel : ViewModel() {
    val externalDirs: List<File>

    //TODO: crash if no externaldir
    //currentExternalDir is root of of current direcotry ex: /storage/emulated/0 or /storage/sdcard
    private val _currentExternalDirIndex = MutableLiveData(0)
    val currentExternalDirIndex: LiveData<Int> = _currentExternalDirIndex
    val currentExternalDir: LiveData<File>

    //currentDir is current visited dir
    private val _currentDir: MutableLiveData<File>
    val currentDir: LiveData<File>

    init {
        externalDirs = App.instance.applicationContext.getExternalFilesDirs(null).toList().map { getBaseDir(it) }

        currentExternalDir = Transformations.map(currentExternalDirIndex) {
            externalDirs[it]
        }

        _currentDir = currentExternalDir as MutableLiveData<File>
        currentDir = _currentDir
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

    fun onDirChange(file: File) {
        if(file.canRead()) {
            _currentDir.value = file
        }
    }

}