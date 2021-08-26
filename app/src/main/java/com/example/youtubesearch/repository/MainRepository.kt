package com.example.youtubesearch.repository

import com.example.youtubesearch.api.RetrofitInstance

class MainRepository {

    suspend fun searchForVideos(query: String, pageToken: String) =
        RetrofitInstance.api.searchForVideos(q = query, pageToken = pageToken)

}