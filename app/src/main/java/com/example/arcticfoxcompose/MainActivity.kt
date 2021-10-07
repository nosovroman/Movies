package com.example.arcticfoxcompose

//import khttp.get

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arcticfoxcompose.ui.theme.*
import okhttp3.*
import java.io.IOException


object SampleData {
    var conversationSample = mutableListOf(
        Message(
            "Colleague",
            "Test...Test...Test..."
        ),
        Message(
            "Colleague",
            "Hey, take a look at Jetpack Compose, it's great!"
        ),
    )
}

class MainActivity : ComponentActivity() {

    //private val viewModel: MovieListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //khttp.get("http://httpbin.org/get")
            ArcticFoxComposeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    //val movies = viewModel.movies.value
    var textState = remember { mutableStateOf("") }
    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        //val movies = viewModel.movies.value
//        for (movie in movies) {
//            Log.d("ФункцияПолучения", "SHAZAM3 ${movies.last().body}")
//        }

        Spacer(modifier = Modifier.height(10.dp))
        SearchFieldView(state = textState)
        Spacer(modifier = Modifier.height(10.dp))
        //MovieListView(messages = viewModel.movies.value)
        //val x = textState.value.text
        MovieListView(messages = getMoviesByRequest(textState.value), state = textState)
    }

    run("https://api.github.com/users/Evin1-/repos")
}

fun run(url: String) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {}
        override fun onResponse(call: Call, response: Response) {
            Log.d("ФункцияRun", "SHAZAM123 ${response.body()?.string()}")
        } //println(response.body()?.string())
    })
}

@Composable
fun MovieListView(messages: MutableList<Message>, state: MutableState<String>) {
    var searchedText = state.value

    LazyColumn {
        //searchedText = state.value
        itemsIndexed(messages) { index, message ->
            if (searchedText == "") { MessageCardView(message) }
            else { MessageCardView(message) }
            if (index < messages.size - 1) Divider( color = SearchLineColorStart, thickness = 1.dp )
        }
    }
}

@Composable
fun SearchFieldView(state: MutableState<String>) {
    var searchLineState = remember { mutableStateOf("") }

    fun updateListMovies (value: String) {
        state.value = value
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Invisible,
                //textColor = Color.Gray,
                //disabledTextColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                //.height(55.dp)
                //.padding(top = 10.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = SearchLineColorEnd,
                    //brush = Brush.horizontalGradient(listOf(SearchLineColorStart, SearchLineColorEnd)),
                    shape = RoundedCornerShape(10.dp)
                )
            ,
            keyboardActions = KeyboardActions(onDone = {
                updateListMovies(searchLineState.value)
            }),
            value = searchLineState.value,
            onValueChange = {
                searchLineState.value = it
                if (searchLineState.value == "") updateListMovies(searchLineState.value)//state.value = searchLineState.value
            },
            singleLine = true,
            textStyle = TextStyle(color = SearchLineTxtColor, fontSize = 20.sp),
            placeholder = { Text(text = "Введите название фильма", color = HintColor) },
            trailingIcon = {
                IconButton(onClick = {
                    updateListMovies(searchLineState.value)
                    //state.value = searchLineState.value

                    //getMoviesByRequest(searchLineState.toString());
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = Purple500,
                        contentDescription = "Search"
                    )
                }
            }
        )
    }
}

@Composable
fun MessageCardView(msg: Message) {
    Row (modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth())//.border(width = 1.dp, color = Color.Blue, shape = RoundedCornerShape(10.dp)))
    {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Image Text",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.5.dp, MaterialTheme.colors.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        )

        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.body2,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
        }
    }
}

data class Message(val author: String, val body: String)

fun getMoviesByRequest (requestMovie: String): MutableList<Message> {
//    SampleData.conversationSample.add(
//        Message(
//            "Тор: Любовь и Гром",
//            "Супер-фильм, который стоит всем посмотреть!"
//        )
//    )

    // фильтрация
    var result = mutableListOf<Message>()
    //Log.d("ФункцияПолучения", "SHAZAM! requestMovie is /${requestMovie}/")
    if (requestMovie != "") {
        for (temp in SampleData.conversationSample) {
            //Log.d("ФункцияПолучения", "SHAZAM0 ${temp.body}, by $requestMovie")
            if (temp.body.contains(requestMovie, ignoreCase = true)) {
                result.add(temp)
            }
        }

//        //  робит со списками, необходимо преобразование
//        var temp = SampleData.conversationSample.filter { curMovie ->
//            curMovie.author.contains(requestMovie)
//        }
//        temp.toMutableList()
    }
    else {
        result = SampleData.conversationSample
        //Log.d("ФункцияПолучения", "SHAZAM1")
    }

//    for (movie in SampleData.conversationSample) {
//        //Log.d("ФункцияПолучения", "SHAZAM2 ${movie.body}")
//    }

    return result
}

//@Preview
//@Composable
//fun PreviewConversation() {
//    ArcticFoxComposeTheme {
//        val textState = remember { mutableStateOf(TextFieldValue("")) }
//        //Column (modifier = Modifier.background(Color.Black))
//        Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
//            Spacer(modifier = Modifier.height(10.dp))
//            SearchFieldView(state = textState)
//            Spacer(modifier = Modifier.height(10.dp))
//            MovieListView(messages = SampleData.conversationSample, state = textState)
//        }
//    }
//}

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