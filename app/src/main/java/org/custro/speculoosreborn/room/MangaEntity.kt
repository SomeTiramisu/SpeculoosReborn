package org.custro.speculoosreborn.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = CachedFileEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["coverId"]
    )]
)
data class MangaEntity(
    @PrimaryKey val uri: String,
    @ColumnInfo(index = true) val coverId: String,
    val pageCount: Int,
    val lastPage: Int,
    val order: List<Int>
)