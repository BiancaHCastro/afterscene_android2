package com.afterscene.data.remote

import com.afterscene.data.remote.model.MoodCategoryDto
import com.afterscene.data.remote.model.WorkDto
import com.afterscene.data.remote.model.WorkSuggestionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoodApiService {

    @GET("moods")
    suspend fun getMoods(): List<MoodCategoryDto>

    @GET("moods/{mood_id}/works")
    suspend fun getWorksByMood(
        @Path("mood_id") moodId: Int
    ): List<WorkDto>

    @POST("moods/{mood_id}/works")
    suspend fun suggestWork(
        @Path("mood_id") moodId: Int,
        @Body request: WorkSuggestionRequest
    ): WorkDto
}
