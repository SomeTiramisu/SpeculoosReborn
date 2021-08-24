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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageCache
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils

class PageModel : ViewModel() {
    private val _uri: MutableLiveData<Uri?> = MutableLiveData(null)
    val uri: LiveData<Uri?> = _uri

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    private val _size = MutableLiveData(Pair(0, 0))

    private val _image = MutableLiveData(ImageBitmap(1, 1))
    val image: LiveData<ImageBitmap> = _image

    private val _hiddenSlider = MutableLiveData(false)
    val hiddenSlider: LiveData<Boolean> = _hiddenSlider

    fun onIndexChange(value: Int) {
        Log.d("PageModel", "new index: $value")
        _index.value = value
        genRequest()
    }

    fun onSizeChange(value: Pair<Int, Int>) {
        _size.value = value
    }

    fun onUriChange(value: Uri) {
        if (value != _uri.value) {
            _uri.value = value
            _index.value = 0
        }
        genRequest()
    }

    fun onHiddenSliderChange(value: Boolean) {
        _hiddenSlider.value = value
    }

    private fun genRequest() = viewModelScope.launch {
        val localUri = PageCache.saveData(uri.value!!)
        val req = PageRequest(index.value!!, _size.value!!.first, _size.value!!.second, localUri)
        val it = tiramisuk.get(req)
        Log.d("ImageCallback", "imaged")
        val bitmap = Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(it, bitmap)
        _image.postValue(bitmap.asImageBitmap()) //called from another thread

    }

    companion object {
        val tiramisuk = Tiramisuk() //because ViewModel is cleared when back button pressed
    }
}