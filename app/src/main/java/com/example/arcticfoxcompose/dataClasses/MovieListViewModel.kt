package com.example.arcticfoxcompose.dataClasses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.arcticfoxcompose.SampleData
import com.example.arcticfoxcompose.SampleData1

class MovieListViewModel(): ViewModel() {
    var movies: MutableState<MutableList<Message>> = mutableStateOf(mutableListOf())

    //private val _movies: MutableLiveData<List<Message>> = MutableLiveData()
    //val movies: LiveData<List<Message>> get() = _movies
    init {
        val result = mutableListOf<Message>()
        for (movie in SampleData1.conversationSample) {
            result.add(movie)
        }
        movies.value = result
    }
}