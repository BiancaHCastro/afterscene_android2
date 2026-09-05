package com.afterscene.data.repository

import com.afterscene.data.remote.MoodApiService
import com.afterscene.data.remote.RetrofitClient
import com.afterscene.data.remote.model.MoodCategoryDto
import com.afterscene.data.remote.model.WorkDto
import com.afterscene.data.remote.model.WorkSuggestionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoodRepository(
    private val apiService: MoodApiService = RetrofitClient.moodApiService
) {

    /**
     * Busca todas as categorias de humor da API (GET /moods).
     */
    suspend fun getMoods(): Result<List<MoodCategoryDto>> = withContext(Dispatchers.IO) {
        try {
            val result = apiService.getMoods()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca as obras de um mood específico (GET /moods/{mood_id}/works).
     */
    suspend fun getWorksByMood(moodId: Int): Result<List<WorkDto>> = withContext(Dispatchers.IO) {
        try {
            val result = apiService.getWorksByMood(moodId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sugere e cadastra uma nova obra em um mood (POST /moods/{mood_id}/works).
     */
    suspend fun suggestWork(
        moodId: Int,
        title: String,
        type: String,
        description: String
    ): Result<WorkDto> = withContext(Dispatchers.IO) {
        try {
            val request = WorkSuggestionRequest(
                title = title,
                type = type,
                description = description
            )
            val result = apiService.suggestWork(moodId, request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
