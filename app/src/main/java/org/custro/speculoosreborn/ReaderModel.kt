package org.custro.speculoosreborn

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.PageScheduler
import org.custro.speculoosreborn.libtiramisuk.renderer.Renderer
import org.custro.speculoosreborn.libtiramisuk.renderer.RendererFactory
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.custro.speculoosreborn.libtiramisuk.utils.matToBitmap

class ReaderModel : ViewModel() {
    private var mScheduler: PageScheduler? = null
    private var mRenderer: Renderer? = null
    private var mSchedulerInitJob: Job? = null

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    private val _size = MutableLiveData(Pair(0, 0))
    val size: LiveData<Pair<Int, Int>> = _size

    private val _image = MutableLiveData(ImageBitmap(1, 1))
    val image: LiveData<ImageBitmap> = _image

    private val _isBlackBorders = MutableLiveData(false)
    val isBlackBorders: LiveData<Boolean> = _isBlackBorders

    private val _background = MutableLiveData(ImageBitmap(1, 1))
    val background: LiveData<ImageBitmap> = _background

    private val _hiddenSlider = MutableLiveData(false)
    val hiddenSlider: LiveData<Boolean> = _hiddenSlider

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
        if (i > maxIndex.value!! - 1) {
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

    fun onUriChange(value: Uri) {
        Log.d("PageModel", "onUriChange called")
        if (value != mRenderer?.uri ?: Uri.EMPTY)  {
            _index.value = 0
            _image.value = ImageBitmap(1, 1)
            mSchedulerInitJob = viewModelScope.launch(Dispatchers.Default) {
                initScheduler(value)
                _maxIndex.postValue(mRenderer!!.pageCount)
            }
        }
    }

    private fun initScheduler(uri: Uri? = null) {
        if (mRenderer == null && uri == null) {
            return
        }
        if (uri != null) {
            mRenderer = RendererFactory.create(uri)
        }
        mScheduler = PageScheduler(mRenderer!!)
        genRequest()
    }

    fun onBackgroundChange(value: ImageBitmap) {
        _background.value = value
    }

    fun onHiddenSliderChange(value: Boolean) {
        _hiddenSlider.value = value
    }

    fun onHiddenSliderSwitch() {
        _hiddenSlider.value = !hiddenSlider.value!!
    }

    private fun genRequest() = viewModelScope.launch(Dispatchers.Default) {
        if (mScheduler == null) {
            return@launch
        }
        mSchedulerInitJob?.join()
        mScheduler!!.at(index.value!!, size.value!!.first, size.value!!.second).collectLatest { value ->
            _image.postValue(value.first.asImageBitmap()) //called from another thread
            _isBlackBorders.postValue(value.second.isBlackBorders)
            Log.d("ImageCallback", "imaged")
            mScheduler!!.seekPagesOrdered(index.value!!, size.value!!.first, size.value!!.second)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mRenderer?.close()
    }
}