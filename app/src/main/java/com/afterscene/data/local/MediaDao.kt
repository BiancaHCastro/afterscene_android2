package com.afterscene.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaEntity): Long

    @Update
    suspend fun update(media: MediaEntity)

    @Delete
    suspend fun delete(media: MediaEntity)

    @Query("SELECT * FROM Media ORDER BY id DESC")
    fun getAll(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM Media WHERE id = :id")
    fun getById(id: Long): Flow<MediaEntity?>

    @Query("SELECT * FROM Media WHERE title LIKE :query ORDER BY id DESC")
    fun search(query: String): Flow<List<MediaEntity>>
}
