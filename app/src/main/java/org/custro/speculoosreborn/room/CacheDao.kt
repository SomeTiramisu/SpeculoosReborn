package org.custro.speculoosreborn.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CacheDao {
    @Query("SELECT * FROM cachedFileEntity")
    fun getAll(): LiveData<List<CachedFileEntity>>

    @Query("SELECT * FROM cachedFileEntity")
    fun getAllNow(): List<CachedFileEntity>

    @Query("SELECT * FROM cachedFileEntity WHERE uuid=:uuid")
    fun get(uuid: String): LiveData<CachedFileEntity>

    @Query("SELECT * FROM cachedFileEntity WHERE uuid=:uuid")
    fun getNow(uuid: String): CachedFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg cachedFile: CachedFileEntity)

    @Update
    fun update(cachedFile: CachedFileEntity)

    @Delete
    fun delete(cachedFile: CachedFileEntity)

    @Query("DELETE FROM cachedFileEntity WHERE uuid=:uuid")
    fun delete(uuid: String)
}