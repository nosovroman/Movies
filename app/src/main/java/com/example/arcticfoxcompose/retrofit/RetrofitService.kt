package com.example.arcticfoxcompose.retrofit

import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.dataClasses.Discover
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("discover/movie?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")//f1c1fa32aa618e6adc168c3cc3cc6c46
    fun getDiscover(): Call<Discover>

    @GET("search/movie?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")//f1c1fa32aa618e6adc168c3cc3cc6c46
    fun getSearchDiscover(@Query("query") query: String): Call<Discover>
}//, @Query("language") lan: String