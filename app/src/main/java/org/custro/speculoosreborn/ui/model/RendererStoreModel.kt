package org.custro.speculoosreborn.ui.model

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.utils.CacheUtils

class RendererStoreModel(uri: Uri) : ViewModel() {
    val renderer: LiveData<Renderer> = liveData(Dispatchers.IO) {
        App.instance.contentResolver.openInputStream(uri).use { stream ->
            CacheUtils.save(stream!!, "current")
        }
        val cacheUri = Uri.fromFile(CacheUtils.get("current"))
        Log.d("ReaderModel", "loaded")

        Log.d("ReaderModel", "loaded2")
        if (cacheUri != Uri.EMPTY && cacheUri!!.scheme == "file") {
            val r = RendererFactory.create(cacheUri.toFile())
            emit(r)
            //   _maxIndex.postValue(r.pageCount)
        } else {
            throw Exception("invalid uri")
        }
    }

    override fun onCleared() {
        renderer.value?.close()
        super.onCleared()
    }
}