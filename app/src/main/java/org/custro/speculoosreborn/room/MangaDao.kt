package org.custro.speculoosreborn.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga")
    fun getAll(): LiveData<List<Manga>>

    @Insert
    fun insertAll(vararg mangas: Manga)

    @Delete
    fun delete(manga: Manga)
}