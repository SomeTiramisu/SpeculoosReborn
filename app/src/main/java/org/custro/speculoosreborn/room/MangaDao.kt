package org.custro.speculoosreborn.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga")
    fun getAll(): LiveData<List<Manga>>

    @Insert
    fun insertAll(vararg mangas: Manga)

    @Delete
    fun delete(manga: Manga)

    @Update
    fun updateManga(manga: Manga)

    @Query("DELETE FROM manga WHERE uri = :uri")
    fun deleteUri(uri: String)
}