package org.custro.speculoosreborn.renderer

import android.net.Uri
import java.io.Closeable

interface Renderer: Closeable, AutoCloseable {
    val uri: Uri
    val pageCount: Int
    fun openPage(index: Int): RendererPage
}