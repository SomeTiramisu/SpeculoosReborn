package org.custro.speculoosreborn

import android.graphics.Bitmap
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
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.PageScheduler
import org.custro.speculoosreborn.libtiramisuk.utils.MangaParser
import org.opencv.android.Utils
import org.custro.speculoosreborn.libtiramisuk.utils.matToBitmap
import org.opencv.core.Mat

class ReaderModel : ViewModel() {
    private var mScheduler: PageScheduler? = null
    private var mMangaParser: MangaParser? = null
    private var mSchedulerInitJob: Job? = null

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    private val _size = MutableLiveData(Pair(0, 0))
    val size: LiveData<Pair<Int, Int>> = _size

    private val _image = MutableLiveData(ImageBitmap(1, 1))
    val image: LiveData<ImageBitmap> = _image

    private val _background = MutableLiveData(ImageBitmap(1, 1))
    val background: LiveData<ImageBitmap> = _background

    private val _hiddenSlider = MutableLiveData(false)
    val hiddenSlider: LiveData<Boolean> = _hiddenSlider

    fun onIndexChange(value: Int) {
        Log.d("PageModel", "new index: $value")
        if (value != index.value) {
            _index.value = value
            genRequest()
        }
    }

    fun onSizeChange(value: Pair<Int, Int>) {
        Log.d("PageModel", "OnSizeChange called, new size: ${value.first}:${value.second}")
        if (value != size.value) {
            _size.value = value
            //_image.value = ImageBitmap(1, 1) //to avoid flicker
            mSchedulerInitJob = viewModelScope.launch(Dispatchers.Default) {
                initScheduler()
            }
        }
    }

    fun onUriChange(value: Uri) {
        Log.d("PageModel", "onUriChange called")
        if (value != mMangaParser?.uri ?: Uri.EMPTY)  {
            _index.value = 0
            _image.value = ImageBitmap(1, 1)
            mSchedulerInitJob = viewModelScope.launch(Dispatchers.Default) {
                initScheduler(value)
                _maxIndex.postValue(mMangaParser!!.size)
            }
        }
    }

    private fun initScheduler(uri: Uri? = null) {
        if (mMangaParser == null && uri == null) {
            return
        }
        if (uri != null) {
            mMangaParser = MangaParser(uri)
        }
        mScheduler = PageScheduler(mMangaParser!!, size.value!!.first, size.value!!.second)
        genRequest()
    }

    fun onBackgroundChange(value: ImageBitmap) {
        _background.value = value
    }

    fun onHiddenSliderChange(value: Boolean) {
        _hiddenSlider.value = value
    }

    private fun genRequest() = viewModelScope.launch(Dispatchers.Default) {
        if (mScheduler == null) {
            return@launch
        }
        mSchedulerInitJob?.join()
        val it = mScheduler!!.at(index.value!!)
        Log.d("ImageCallback", "imaged")
        _image.postValue(matToBitmap(it).asImageBitmap()) //called from another thread
        mScheduler!!.seekPages(index.value!!)
    }


}