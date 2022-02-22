package org.custro.speculoosreborn.renderer

data class RenderInfo(val isBlackBorders: Boolean)

data class RenderConfig(
    val addBorders: Boolean,
    val doScale: Boolean,
    val doCrop: Boolean,
    val doMask: Boolean
)