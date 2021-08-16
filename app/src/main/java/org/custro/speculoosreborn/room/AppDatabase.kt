package org.custro.speculoosreborn.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Manga::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun mangaDao(): MangaDao
}