package org.custro.speculoosreborn.renderer

import org.opencv.core.Mat
import java.io.Closeable

interface RendererPage: Closeable, AutoCloseable {
    fun render(img: Mat, width: Int, height: Int, config: RenderConfig): RenderInfo
}
