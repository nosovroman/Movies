package com.example.arcticfoxcompose.common

import com.example.arcticfoxcompose.retrofit.RetrofitClient
import com.example.arcticfoxcompose.retrofit.RetrofitService

object Common {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "f1c1fa32aa618e6adc168c3cc3cc6c46"
    const val LANGUAGE = "ru"
    const val BASE_URL_IMAGES = "http://image.tmdb.org/t/p/"
    const val POSTER_SIZE = "w154"
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)

    const val LOAD_STATE_NOTHING = 0
    const val LOAD_STATE_SOMETHING = 1

    const val FETCH_DISCOVER = "discover"
    const val FETCH_SEARCH = "search"
}