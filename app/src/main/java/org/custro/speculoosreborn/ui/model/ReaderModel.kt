package org.custro.speculoosreborn.ui.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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

class ReaderModel : ViewModel() {
    private var mScheduler: PageScheduler? = null
    private var mRenderer: Renderer? = null

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    private val _size = MutableLiveData(Pair(0, 0))
    val size: LiveData<Pair<Int, Int>> = _size

    private val _image = MutableLiveData(emptyBitmap())
    val image: LiveData<Bitmap> = _image

    private val _isBlackBorders = MutableLiveData(false)
    val isBlackBorders: LiveData<Boolean> = _isBlackBorders

    init {
        Log.d("ReaderModel", "new viewmodel")
    }


    fun onIndexChange(value: Int) {
        Log.d("PageModel", "new index: $value")
        val normValue = normIndex(value)
        if (normValue != index.value) {
            _index.value = normValue
            genRequest()
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

    fun onSizeChange(value: Pair<Int, Int>) {
        Log.d("PageModel", "OnSizeChange called, new size: ${value.first}:${value.second}")
        if (value != size.value) {
            _size.value = value
            //_image.value = ImageBitmap(1, 1) //to avoid flicker
            genRequest()
        }
    }

    //must be called before everything else. Called once
    fun onUriChange(value: Uri) {
        Log.d("PageModel", "onUriChange called")
        //TODO: fix reload
        if (mRenderer == null && value != Uri.EMPTY)  {
            _index.value = 0
            mRenderer = RendererFactory.create(value)
            _maxIndex.value = mRenderer!!.pageCount
            mScheduler = PageScheduler(mRenderer!!)
            genRequest()
        }
    }

    private fun genRequest() {
        if(mScheduler == null) {
            return
        }
        Log.d("ReaderModel", "requesting ${index.value}")
        viewModelScope.launch {
            Log.d("ReaderModel", "requesting in viewmodelScope")
            //launch {
                val res = mScheduler!!.at(index.value!!, size.value!!.first, size.value!!.second)
                _image.postValue(res.first) //called from another thread
                _isBlackBorders.postValue(res.second.isBlackBorders)
                Log.d("ImageCallback", "imaged")
           // }
           // launch {
                mScheduler!!.seekPagesOrdered(
                    index.value!!,
                    size.value!!.first,
                    size.value!!.second
                )

           // }
        }
    }

    override fun onCleared() {
        mRenderer?.close()
        super.onCleared()
    }
}