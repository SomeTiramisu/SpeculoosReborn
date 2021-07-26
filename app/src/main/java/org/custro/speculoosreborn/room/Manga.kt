package org.custro.speculoosreborn.room

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    @PrimaryKey val uri: String,
    val cover: ByteArray?,
    val maxIndex: Int
)