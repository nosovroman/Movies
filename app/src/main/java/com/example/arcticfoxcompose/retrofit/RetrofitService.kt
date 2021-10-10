package com.example.arcticfoxcompose.retrofit

import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.dataClasses.Discover
import com.example.arcticfoxcompose.dataClasses.Gallery
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
        // получение общего списка фильмов
    @GET("discover/movie?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getDiscover(): Call<Discover>
        // получение списка фильмов по запросу
    @GET("search/movie?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getSearchDiscover(@Query("query") query: String): Call<Discover>

        // получение галереи фильма /movie/{movie_id}/images
    @GET("movie/{movie_id}/images?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getGallery(@Path("movie_id") movieId: Int): Call<Gallery>

}//, @Query("language") lan: String