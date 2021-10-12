package com.example.arcticfoxcompose.retrofit


import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.dataClasses.Actors.Actors
import com.example.arcticfoxcompose.dataClasses.DetailMovie.DetailMovie
import com.example.arcticfoxcompose.dataClasses.Discover
import com.example.arcticfoxcompose.dataClasses.Gallery
import com.example.arcticfoxcompose.dataClasses.Review.Review
import com.example.arcticfoxcompose.dataClasses.Video.Video
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

        // получение детальной информации о фильме
    @GET("movie/{movie_id}?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getDetailMovie(@Path("movie_id") movieId: Int): Call<DetailMovie>

        // получение рецензии на фильм
    @GET("movie/{movie_id}/reviews?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getReview(@Path("movie_id") movieId: Int): Call<Review>

    // получение рецензии на фильм
    @GET("movie/{movie_id}/credits?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getActors(@Path("movie_id") movieId: Int): Call<Actors>


    // /movie/{movie_id}/videos
    @GET("movie/{movie_id}/videos?api_key=${Common.API_KEY}&language=${Common.LANGUAGE}")
    fun getVideo(@Path("movie_id") movieId: Int): Call<Video>

}//, @Query("language") lan: String