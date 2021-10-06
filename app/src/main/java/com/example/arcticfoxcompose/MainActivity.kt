package com.example.arcticfoxcompose


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.arcticfoxcompose.components.MessageCardView
import com.example.arcticfoxcompose.components.MovieListView
import com.example.arcticfoxcompose.dataClasses.Message
import com.example.arcticfoxcompose.dataClasses.SampleData
import com.example.arcticfoxcompose.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArcticFoxComposeTheme {
                Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SearchFieldView()
                    Spacer(modifier = Modifier.height(10.dp))
                    MovieListView(messages = SampleData.conversationSample)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    ArcticFoxComposeTheme {
        //Column (modifier = Modifier.background(Color.Black))
        Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            Spacer(modifier = Modifier.height(10.dp))
            SearchFieldView()
            Spacer(modifier = Modifier.height(10.dp))
            MovieListView(messages = SampleData.conversationSample)
        }
    }
}

//@Preview(name = "Light Mode")
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    showBackground = true,
//    name = "Dark Mode"
//)

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ArcticFoxComposeTheme {
//        MessageCardView(
//            msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!")
//        )
//    }
//}