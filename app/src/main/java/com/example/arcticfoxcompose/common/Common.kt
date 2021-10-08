package com.example.arcticfoxcompose.common

import com.example.arcticfoxcompose.retrofit.RetrofitClient
import com.example.arcticfoxcompose.retrofit.RetrofitService

object Common {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "f1c1fa32aa618e6adc168c3cc3cc6c46"
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)
}