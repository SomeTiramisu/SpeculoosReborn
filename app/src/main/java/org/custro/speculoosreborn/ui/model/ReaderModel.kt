package org.custro.speculoosreborn.ui.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.scheduler.PageScheduler
import org.custro.speculoosreborn.utils.emptyBitmap

class ReaderModel(uri: Uri) : ViewModel() {
    val renderer: Renderer

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    init {
        //Log.d("ReaderModel", "new viewmodel")
        if(uri != Uri.EMPTY && uri.scheme == "file") {
            _index.value = 0
            renderer = RendererFactory.create(uri.toFile())
            _maxIndex.value = renderer.pageCount
        } else {
            throw Exception("invalid uri")
        }

    }


    fun onIndexChange(value: Int) {
        //Log.d("PageModel", "new index: $value")
        val normValue = normIndex(value)
        if (normValue != index.value) {
            _index.value = normValue
        }
    }

    fun onIndexInc() {
        onIndexChange(index.value!!+1)
    }

    fun onIndexDec() {
        onIndexChange(index.value!!-1)
    }

    private fun normIndex(i: Int): Int {
        if (i < 0) {
            return 0
        }
        if (i > 0 && i > maxIndex.value!! - 1) {
            return maxIndex.value!! - 1
        }
        return i
    }


    override fun onCleared() {
        renderer.close()
        super.onCleared()
    }
}