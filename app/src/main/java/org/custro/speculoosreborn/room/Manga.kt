package org.custro.speculoosreborn.room

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    @PrimaryKey val uri: String,
    val cover: String,
    //val maxIndex: Int,
    //val order: List<Int>
)