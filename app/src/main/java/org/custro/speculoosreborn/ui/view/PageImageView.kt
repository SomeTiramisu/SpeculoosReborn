package org.custro.speculoosreborn.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.renderer.RenderConfig
import org.custro.speculoosreborn.renderer.Renderer
import org.custro.speculoosreborn.ui.model.ReaderPageModel
import org.opencv.android.Utils
import org.opencv.core.Mat

class PageImageView(context: Context) : AppCompatImageView(context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    var renderer: Renderer? = null
        set(value) {
            field = value
            render()
        }

    var index: Int? = null
        set(value) {
            field = value
            render()
        }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        render()
    }

    private fun render() {
        scope.launch {
            if (renderer != null && index != null) {
                renderer!!.openPage(index!!).use {
                    val img = Mat()
                    val config = RenderConfig(
                        addBorders = index != 0,
                        doScale = true,
                        doCrop = index != 0,
                        doMask = true
                    )
                    val renderInfo = it.render(img, width, height, config)
                    val bitmap =
                        Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888)
                    Utils.matToBitmap(img, bitmap)
                    // _isBlackBorders.postValue(renderInfo.isBlackBorders)
                    setImageBitmap(bitmap)
                }
            }
        }
    }


}