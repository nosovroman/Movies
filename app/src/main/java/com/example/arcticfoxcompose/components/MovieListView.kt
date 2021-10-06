package com.example.arcticfoxcompose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.arcticfoxcompose.dataClasses.Message
import com.example.arcticfoxcompose.ui.theme.SearchLineColorStart

@Composable
fun MovieListView(messages: List<Message>) {
    LazyColumn {
        itemsIndexed(messages) { index, message ->
            Column {
                MessageCardView(message)
                if (index < messages.size - 1) {
                    Divider(
                        color = SearchLineColorStart, thickness = 1.dp, //startIndent = 10.dp,
                        //modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    )
                }
            }
        }
    }
}