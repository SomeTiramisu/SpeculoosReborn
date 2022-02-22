package org.custro.speculoosreborn.renderer

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import org.opencv.core.Mat

class EmptyRendererPage: RendererPage {
    override fun render(img: Mat, width: Int, height: Int, config: RenderConfig): RenderInfo {
        return RenderInfo(false)
    }

    override fun close() {
    }
}