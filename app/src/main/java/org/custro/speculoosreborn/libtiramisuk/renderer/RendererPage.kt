package org.custro.speculoosreborn.libtiramisuk.renderer

import android.graphics.Bitmap
import java.io.Closeable

interface RendererPage: Closeable, AutoCloseable {
    fun render(bitmap: Bitmap): RenderInfo
}
