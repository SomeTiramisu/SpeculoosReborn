package org.custro.speculoosreborn.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDao {
    @Query("SELECT * FROM cachedFileEntity")
    suspend fun getAll(): List<CachedFileEntity>

    //@Query("SELECT * FROM cachedFileEntity")
    //fun getAllNow(): List<CachedFileEntity>

    @Query("SELECT * FROM cachedFileEntity WHERE uuid=:uuid")
    suspend fun get(uuid: String): CachedFileEntity

    //@Query("SELECT * FROM cachedFileEntity WHERE uuid=:uuid")
    //fun getNow(uuid: String): CachedFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg cachedFile: CachedFileEntity)

    @Update
    fun update(cachedFile: CachedFileEntity)

    @Delete
    fun delete(cachedFile: CachedFileEntity)

    @Query("DELETE FROM cachedFileEntity WHERE uuid=:uuid")
    fun delete(uuid: String)
}