package com.afterscene.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkDto(
    @param:Json(name = "id") @field:Json(name = "id") val id: Int,
    @param:Json(name = "title") @field:Json(name = "title") val title: String,
    @param:Json(name = "type") @field:Json(name = "type") val type: String,
    @param:Json(name = "description") @field:Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class MoodCategoryDto(
    @param:Json(name = "id") @field:Json(name = "id") val id: Int,
    @param:Json(name = "name") @field:Json(name = "name") val name: String,
    @param:Json(name = "emoji") @field:Json(name = "emoji") val emoji: String? = null,
    @param:Json(name = "description") @field:Json(name = "description") val description: String? = null,
    @param:Json(name = "works") @field:Json(name = "works") val works: List<WorkDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WorkSuggestionRequest(
    @param:Json(name = "title") @field:Json(name = "title") val title: String,
    @param:Json(name = "type") @field:Json(name = "type") val type: String,
    @param:Json(name = "description") @field:Json(name = "description") val description: String
)
