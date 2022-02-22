package org.custro.speculoosreborn.ui.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.single
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.renderer.RendererPage
import org.custro.speculoosreborn.scheduler.PageScheduler
import org.custro.speculoosreborn.utils.CacheUtils
import org.custro.speculoosreborn.utils.emptyBitmap

class ReaderModel(uri: Uri) : ViewModel() {
    val renderer: LiveData<Renderer> = liveData(Dispatchers.IO) {
        App.instance.contentResolver.openInputStream(uri).use { stream ->
            CacheUtils.save(stream!!, "current")
            //CacheUtils.save(stream!!, uuid)
        }
        val cacheUri = Uri.fromFile(CacheUtils.get("current"))
        Log.d("ReaderModel", "loaded")

        Log.d("ReaderModel", "loaded2")
        if(cacheUri != Uri.EMPTY && cacheUri!!.scheme == "file") {
            val r = RendererFactory.create(cacheUri!!.toFile())
            _index.postValue(0)
            emit(r)
            _maxIndex.postValue(r.pageCount)
        } else {
            throw Exception("invalid uri")
        }
    }

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

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
        renderer.value?.close()
        super.onCleared()
    }
}