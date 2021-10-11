package com.example.arcticfoxcompose.common

import android.os.Build
import com.example.arcticfoxcompose.retrofit.RetrofitClient
import com.example.arcticfoxcompose.retrofit.RetrofitService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Common {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "f1c1fa32aa618e6adc168c3cc3cc6c46"
    const val LANGUAGE = "ru"
    const val BASE_URL_IMAGES = "http://image.tmdb.org/t/p/"
    const val POSTER_SIZE_LIST = "w154" // "w92","w154","w185","w342","w500","w780","original" http://image.tmdb.org/t/p/w185/670x9sf0Ru8y6ezBggmYudx61yB.jpg
    const val POSTER_SIZE_MOVIE = "w342"
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)

    const val LOAD_STATE_NOTHING = 0
    const val LOAD_STATE_SOMETHING = 1

    const val FETCH_DISCOVER = "discover"
    const val FETCH_SEARCH = "search"
}

fun String.formatDate(): String {
    return if (this != null && this != "") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val x = LocalDate.parse(this)
            x.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } else {
            this
        }
    } else {
        "-"
    }
}