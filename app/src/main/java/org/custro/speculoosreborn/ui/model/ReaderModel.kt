package org.custro.speculoosreborn.ui.model

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.renderer.RendererFactory
import org.custro.speculoosreborn.utils.CacheUtils

class ReaderModel(uri: Uri) : ViewModel() {
    val pageCount: LiveData<Int> = liveData(Dispatchers.IO) {
        emit(App.db.mangaDao().getPageCount(uri.toString()))
    }

}