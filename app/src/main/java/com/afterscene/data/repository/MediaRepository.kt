package com.afterscene.data.repository

import com.afterscene.data.local.MediaDao
import com.afterscene.data.local.MediaEntity
import kotlinx.coroutines.flow.Flow

class MediaRepository(private val mediaDao: MediaDao) {
    val allMedia: Flow<List<MediaEntity>> = mediaDao.getAll()

    fun getById(id: Long): Flow<MediaEntity?> {
        return mediaDao.getById(id)
    }

    fun search(query: String): Flow<List<MediaEntity>> {
        return if (query.isEmpty()) {
            mediaDao.getAll()
        } else {
            mediaDao.search("%$query%")
        }
    }

    suspend fun insert(media: MediaEntity): Long {
        return mediaDao.insert(media)
    }

    suspend fun update(media: MediaEntity) {
        mediaDao.update(media)
    }

    suspend fun delete(media: MediaEntity) {
        mediaDao.delete(media)
    }
}
