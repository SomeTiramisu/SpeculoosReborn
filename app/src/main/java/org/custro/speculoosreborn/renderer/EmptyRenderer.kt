package org.custro.speculoosreborn.renderer

class EmptyRenderer: Renderer {
    override val pageCount: Int = 0

    override fun openPage(index: Int): RendererPage {
        return EmptyRendererPage()
    }

    override fun close() {
    }
}