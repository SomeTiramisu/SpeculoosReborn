package org.custro.speculoosreborn.renderer

import android.graphics.Bitmap
import java.io.Closeable

interface RendererPage: Closeable, AutoCloseable {
    fun render(bitmap: Bitmap, config: RenderConfig): RenderInfo
}