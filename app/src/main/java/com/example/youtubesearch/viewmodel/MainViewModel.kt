package com.example.youtubesearch.viewmodel

import android.accounts.NetworkErrorException
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.youtubesearch.application.YouTubeApplication
import com.example.youtubesearch.data.YouTubeData
import com.example.youtubesearch.repository.MainRepository
import com.example.youtubesearch.util.Resource
import okio.IOException
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val repository: MainRepository

    private val _searchVideos: MutableLiveData<Resource<YouTubeData>> = MutableLiveData()
    val searchVideos: LiveData<Resource<YouTubeData>> = _searchVideos

    var searchVideoPage = 0
    private var searchVideoResponse: YouTubeData? = null
    private var pageTokens: MutableList<String> = mutableListOf()
    var oldSearchQuery: String? = null

    init {
        repository = MainRepository()
    }

    suspend fun getSearchVideos(query: String, isPrevious: Boolean){
        _searchVideos.postValue(Resource.Loading())
        try {
            val response = if(oldSearchQuery == query){
                if(isPrevious){
                    repository.searchForVideos(query, pageTokens[pageTokens.lastIndex - 2])
                }else{
                    repository.searchForVideos(query, pageTokens[pageTokens.lastIndex])
                }
            } else {
                pageTokens = mutableListOf()
                pageTokens.add("")
                searchVideoPage = 0
                oldSearchQuery = query
                repository.searchForVideos(query,"")
            }
            _searchVideos.postValue(handleSearchVideosResult(response, isPrevious))
        } catch (t: Throwable){
            t.printStackTrace()
            when(t){
                is NetworkErrorException -> _searchVideos.postValue(Resource.Error("Needs Internet Connection"))
                is IOException -> _searchVideos.postValue(Resource.Error("Network Failure"))
                else -> _searchVideos.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleSearchVideosResult(response: Response<YouTubeData>, isPrevious: Boolean): Resource<YouTubeData> {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchVideoResponse==null){
                    searchVideoPage=1
                    pageTokens.add(resultResponse.nextPageToken)
                }else {
                    if (isPrevious) {
                        searchVideoPage--
                        pageTokens.removeAt(pageTokens.lastIndex)
                    } else {
                        searchVideoPage++
                        pageTokens.add(resultResponse.nextPageToken)
                    }
                }
                searchVideoResponse=resultResponse
                return Resource.Success(searchVideoResponse!!)
            }
        }
        return Resource.Error(response.message())
    }

}