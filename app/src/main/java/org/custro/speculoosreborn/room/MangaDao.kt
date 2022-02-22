package org.custro.speculoosreborn.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM mangaEntity")
    suspend fun getAll(): List<MangaEntity>

    @Insert
    fun insert(vararg mangas: MangaEntity)

    @Delete
    fun delete(manga: MangaEntity)

    @Update
    fun update(manga: MangaEntity)

    @Query("DELETE FROM mangaEntity WHERE uri = :uri")
    fun delete(uri: String)
}