package org.custro.speculoosreborn.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface MangaDao {
    @Query("SELECT * FROM mangaEntity")
    suspend fun getAll(): List<MangaEntity>

    @Query("SELECT * FROM mangaEntity")
    fun getAllFlow(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM mangaEntity")
    fun getAllLiveData(): LiveData<List<MangaEntity>>

    //Note: keeps refreshing in the background
    fun getDistinctAllFlow() = getAllFlow().distinctUntilChanged()

    @Query("SELECT pageCount FROM mangaEntity WHERE uri = :uri")
    suspend fun getPageCount(uri: String): Int

    @Insert
    fun insert(vararg mangas: MangaEntity)

    @Delete
    fun delete(manga: MangaEntity)

    @Update
    fun update(manga: MangaEntity)

    @Query("DELETE FROM mangaEntity WHERE uri = :uri")
    fun delete(uri: String)
}