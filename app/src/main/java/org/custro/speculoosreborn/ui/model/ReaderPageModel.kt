package org.custro.speculoosreborn.ui.model

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.utils.emptyBitmap
import org.opencv.android.Utils
import org.opencv.core.Mat

class ReaderPageModel(private val index: Int) : ViewModel() {
    var renderer: Renderer? = null
        set(value) {
            field = value
            render()
        }

    private val config = RenderConfig(
        addBorders = index != 0,
        doScale = true,
        doCrop = index != 0,
        doMask = true
    )

    var size = Pair(1080, 1920)
        set(value) {
            if(value != field) {
                field = value
                render()
            }
        }

    private val _image = MutableLiveData(emptyBitmap())
    val image: LiveData<Bitmap> = _image

    private val _isBlackBorders = MutableLiveData(false)
    val isBlackBorders: LiveData<Boolean> = _isBlackBorders

    private fun render() {
        viewModelScope.launch(Dispatchers.Default) {
            renderer?.openPage(index)?.use {
                val img = Mat()
                val renderInfo = it.render(img, size.first, size.second, config)
                val bitmap =
                    Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(img, bitmap)
                _isBlackBorders.postValue(renderInfo.isBlackBorders)
                _image.postValue(bitmap)
            }
        }
    }

}