package org.custro.speculoosreborn.renderer

import java.io.Closeable

interface Renderer : Closeable, AutoCloseable {
    val pageCount: Int
    fun openPage(index: Int): RendererPage
}