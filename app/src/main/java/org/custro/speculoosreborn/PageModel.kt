package org.custro.speculoosreborn

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.custro.speculoosreborn.libtiramisuk.Tiramisuk
import org.custro.speculoosreborn.libtiramisuk.utils.PageRequest
import org.opencv.android.Utils
import java.io.File

class PageModel : ViewModel() {
    val emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val tiramisuk = Tiramisuk()

    private val _file: MutableLiveData<File?> = MutableLiveData(null)
    val file: LiveData<File?> = _file

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index

    private val _maxIndex = MutableLiveData(0)
    val maxIndex: LiveData<Int> = _maxIndex

    private val _size = MutableLiveData(Pair(0, 0))

    private val _image = MutableLiveData(emptyBitmap)
    val image: LiveData<Bitmap> = _image

    init {
        tiramisuk.connectImageCallback {
            Log.d("ImageCallback", "imaged")
            val bitmap = Bitmap.createBitmap(it.cols(), it.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(it, bitmap)
            _image.postValue(bitmap) //called from another thread
        }
        tiramisuk.connectMaxIndexCallback {
            Log.d("SizeCallback", "sized: $it")
            _maxIndex.postValue(it) //same here maybe ?
        }
    }

    fun onIndexChange(value: Int) {
        Log.d("PageModel", "new index: $value")
        _index.value = value
        genRequest()
    }

    fun onSizeChange(value: Pair<Int, Int>) {
        _size.value = value
        //genRequest()
    }

    fun onFileChange(value: File) {
        _file.value = value
        _index.value = 0
        genRequest()
    }

    private fun genRequest() {
        val req = PageRequest(index.value!!, _size.value!!.first, _size.value!!.second, file.value!!)
        tiramisuk.get(req)
    }
}