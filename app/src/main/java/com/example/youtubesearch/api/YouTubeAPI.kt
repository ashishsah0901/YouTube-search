package com.example.youtubesearch.api

import com.example.youtubesearch.data.YouTubeData
import com.example.youtubesearch.util.Constants.API_KEY
import com.example.youtubesearch.util.Resource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeAPI {

    @GET("v3/search")
    suspend fun searchForVideos(
        @Query("part")
        part: String = "snippet",
        @Query("maxResults")
        maxResults: Int = 10,
        @Query("q")
        q: String = "",
        @Query("pageToken")
        pageToken: String = "",
        @Query("key")
        key: String = API_KEY
    ): Response<YouTubeData>

}