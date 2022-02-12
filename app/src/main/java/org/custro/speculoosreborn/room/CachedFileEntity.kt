package org.custro.speculoosreborn.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CachedFileEntity(
    @PrimaryKey val uuid: String,
    val path: String, //path in cache dir
    val lastAccess: Long,
    val name: String, //original file name
)
