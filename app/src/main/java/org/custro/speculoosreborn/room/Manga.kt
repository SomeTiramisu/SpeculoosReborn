package org.custro.speculoosreborn.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Manga(
    @PrimaryKey val uri: String,
    //val cover: ByteArray?,
    //val maxIndex: Int,
    //val order: List<Int>
)