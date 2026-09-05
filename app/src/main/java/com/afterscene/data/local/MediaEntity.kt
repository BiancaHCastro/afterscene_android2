package com.afterscene.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Media")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val type: String,
    val genre: String,
    val description: String,
    val emotion: String,
    val rating: Int,
    val imageUri: String?
)
