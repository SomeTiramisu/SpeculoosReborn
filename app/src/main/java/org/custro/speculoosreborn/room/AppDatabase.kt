package org.custro.speculoosreborn.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MangaEntity::class, CachedFileEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun cacheDao(): CacheDao
}