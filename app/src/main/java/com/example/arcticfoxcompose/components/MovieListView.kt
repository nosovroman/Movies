package com.example.arcticfoxcompose.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.arcticfoxcompose.dataClasses.Message
import com.example.arcticfoxcompose.ui.theme.SearchLineColorStart

@Composable
fun MovieListView(messages: List<Message>, state: MutableState<TextFieldValue>) {
    var searchedText = state.value

    //var requestMoviesListener = remember { mutableStateOf(true) }
    //val messages = messages.toMutableStateList()
    //var mes by mutableStateListOf<Message>(messages)//remember { mutableStateListOf(messages) }

    LazyColumn {
        searchedText = state.value
        itemsIndexed(messages) { index, message ->
            MessageCardView(message)
            if (index < messages.size - 1) Divider( color = SearchLineColorStart, thickness = 1.dp )
        }
    }
}