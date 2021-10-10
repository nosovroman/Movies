package com.example.arcticfoxcompose.dataClasses

data class Gallery(
    val backdrops: List<Any>,
    val id: Int,
    val logos: List<Any>,
    val posters: List<Poster>
)