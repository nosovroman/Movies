package com.example.arcticfoxcompose

import android.os.Build
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.dataClasses.Discover
import com.example.arcticfoxcompose.dataClasses.Result
import com.example.arcticfoxcompose.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//const val BASE_URL = "https://api.themoviedb.org/3/"
//const val BASE_URL = "https://jsonplaceholder.typicode.com/"

//object SampleData {
//    var conversationSample = mutableListOf(
//        Message(
//            "Colleague",
//            "Test...Test...Test..."
//        ),
//        Message(
//            "Colleague",
//            "Hey, take a look at Jetpack Compose, it's great!"
//        ),
//    )
//}

class MainActivity : ComponentActivity() {

    //private val viewModel: MovieListViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArcticFoxComposeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    //val movies = viewModel.movies.value
    var textState = remember { mutableStateOf(mutableListOf<Result>()) }
    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        //val movies = viewModel.movies.value
//        for (movie in movies) {
//            Log.d("ФункцияПолучения", "SHAZAM3 ${movies.last().body}")
//        }

        Spacer(modifier = Modifier.height(10.dp))
        SearchFieldElem(state = textState)
        Spacer(modifier = Modifier.height(10.dp))
        //MovieListView(messages = viewModel.movies.value)
        //val x = textState.value.text
        //MovieListView(messages = getMoviesByRequest(textState.value), state = textState)
        MovieListElem(state = textState)
    }

    getMyDiscover(state = textState)
    //getMySearchDiscover(state = textState, request = "Веном")
}

// --- получение фильмов
// перезапись: state
fun getMyDiscover(state: MutableState<MutableList<Result>>) {
    val movies = mutableListOf<Result>()

    Common.retrofitService.getDiscover().enqueue(
        object : Callback<Discover> {
            override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
                //Log.d("MyDiscover", "Hello bro")
                val responseBody = response.body()!!.results
                val myStringBuilder = StringBuilder()
                for (myData in responseBody) {
                    myStringBuilder.append("${myData.title}\n")
                    movies.add(myData)
                }
                Log.d("MyDiscover", "END: \n $myStringBuilder")
                state.value = movies
            }

            override fun onFailure(call: Call<Discover>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: "+ t.message)
            }
        }
    )
}

// --- получение фильмов
// перезапись: state
fun getMySearchDiscover(request: String, state: MutableState<MutableList<Result>>) {
    val movies = mutableListOf<Result>()

    Log.d("MyDiscover", "Hello bro212")
    Common.retrofitService.getSearchDiscover(query = request).enqueue(
        object : Callback<Discover> {
            override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
//                Log.d("MyDiscover", "Hello bro")
                val responseBody = response.body()!!.results
                val myStringBuilder = StringBuilder()
                for (myData in responseBody) {
                    myStringBuilder.append("${myData.title}\n")
                    movies.add(myData)
                }
                Log.d("MyDiscover", "END: \n $myStringBuilder")
                state.value = movies
            }

            override fun onFailure(call: Call<Discover>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: "+ t.message)
            }
        }
    )
}


// список фильмов
@Composable
fun MovieListElem(state: MutableState<MutableList<Result>>) {
    var movies = state.value

    LazyColumn {
        itemsIndexed(movies) { index, movie ->
            MovieCardElem(movie)
            if (index < movies.size - 1) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Divider( color = SearchLineColorStart, thickness = 1.dp )
            }
        }
    }
}

// строка поиска
@Composable
fun SearchFieldElem(state: MutableState<MutableList<Result>>) {
    var searchLineState = remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
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
                ),
            keyboardActions = KeyboardActions(onDone = {
                //updateListMovies(searchLineState.value) ----------------------- ФИЛЬТРАЦИЯ
                if (searchLineState.value.trim() != "") {
                    getMySearchDiscover(request = searchLineState.value, state = state)
                }
//                else {
//                    getMyDiscover(state = state)
//                }
            }),
            value = searchLineState.value,
            onValueChange = {
                searchLineState.value = it
                if (searchLineState.value == "") getMyDiscover(state = state)
            },
            singleLine = true,
            textStyle = TextStyle(color = SearchLineColorEnd, fontSize = 20.sp),
            placeholder = { Text(text = "Введите название фильма", color = HintColor) },
            trailingIcon = {
                IconButton(onClick = {
                    if (searchLineState.value.trim() != "") {
                        getMySearchDiscover(request = searchLineState.value, state = state)
                    }
                    //updateListMovies(searchLineState.value) ----------------------- ФИЛЬТРАЦИЯ

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

// конкретный фильм
@Composable
fun MovieCardElem(movie: Result) {
    val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val x = LocalDate.parse(movie.release_date)
        x.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } else {
        movie.release_date
    }

    Row (modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth())//.border(width = 1.dp, color = Color.Blue, shape = RoundedCornerShape(10.dp)))
    {
            // картинка фильма

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                //painter = painterResource(id = R.drawable.default_image),
                painter = rememberImagePainter("${Common.BASE_URL_IMAGES}${Common.POSTER_SIZE}${movie.poster_path}"),
                contentDescription = "Image Text",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    //.size(40.dp)
                    .width(120.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        2.5.dp, MaterialTheme.colors.secondary, shape = RoundedCornerShape(10.dp)
                    )
            )
        }


        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }

        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        )
            // информация о фильме
        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            // название фильма
            Text(
                text = movie.title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

                // дата выхода (релиза)
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = "Дата релиза: $date",
                    modifier = Modifier.padding(all = 4.dp),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

                // рейтинг
            Text(
                text = "Рейтинг: ${movie.vote_average} ⭐",
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

// ---------------------------------- КОНЕЦ ---------------------------

//data class Message(val author: String, val body: String)

//fun getMoviesByRequest (requestMovie: String): MutableList<Message> {
////    SampleData.conversationSample.add(
////        Message(
////            "Тор: Любовь и Гром",
////            "Супер-фильм, который стоит всем посмотреть!"
////        )
////    )
//
//    // фильтрация
//    var result = mutableListOf<Message>()
//    //Log.d("ФункцияПолучения", "SHAZAM! requestMovie is /${requestMovie}/")
//    if (requestMovie != "") {
//        for (temp in SampleData.conversationSample) {
//            //Log.d("ФункцияПолучения", "SHAZAM0 ${temp.body}, by $requestMovie")
//            if (temp.body.contains(requestMovie, ignoreCase = true)) {
//                result.add(temp)
//            }
//        }
//
////        //  робит со списками, необходимо преобразование
////        var temp = SampleData.conversationSample.filter { curMovie ->
////            curMovie.author.contains(requestMovie)
////        }
////        temp.toMutableList()
//    }
//    else {
//        result = SampleData.conversationSample
//        //Log.d("ФункцияПолучения", "SHAZAM1")
//    }
//
////    for (movie in SampleData.conversationSample) {
////        //Log.d("ФункцияПолучения", "SHAZAM2 ${movie.body}")
////    }
//
//    return result
//}

// ---------------------------------- КОНЕЦ ---------------------------

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