package com.example.arcticfoxcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.common.formatDate
import com.example.arcticfoxcompose.dataClasses.Discover
import com.example.arcticfoxcompose.dataClasses.Result
import com.example.arcticfoxcompose.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArcticFoxComposeTheme {
                AppNavigator()
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "moviesView"
        ) {
            composable("moviesView") { MainScreen(navController) }
            composable(
                "detailMoviesView/{movieId}",
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType }
                )
            ) {
                backStackEntry ->
                backStackEntry?.arguments?.getInt("movieId")?.let { movieId ->
                    DetailMovieScreen(movieId = movieId)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
        var textState = remember { mutableStateOf(mutableListOf<Result>()) }
        var resultOfLoad = remember { mutableStateOf(Common.LOAD_STATE_SOMETHING) }
        Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            Spacer(modifier = Modifier.height(10.dp))
            SearchFieldElem(state = textState, resultOfLoad = resultOfLoad)
            Spacer(modifier = Modifier.height(10.dp))
            MovieListElem(state = textState, navController = navController, resultOfLoad = resultOfLoad)
        }

        getMyDiscover(state = textState, resultOfLoad = resultOfLoad)
}

// --- получение фильмов
// перезапись: state
fun getMyDiscover(state: MutableState<MutableList<Result>>, resultOfLoad: MutableState<Int>) {
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

                if (movies.isEmpty()) resultOfLoad.value = Common.LOAD_STATE_NOTHING
                else resultOfLoad.value = Common.LOAD_STATE_SOMETHING
            }

            override fun onFailure(call: Call<Discover>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: "+ t.message)
            }
        }
    )
}

// --- получение фильмов
// перезапись: state
fun getMySearchDiscover(request: String, state: MutableState<MutableList<Result>>, letShowDialog: MutableState<Boolean>, resultOfLoad: MutableState<Int>) {
    try {
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

                    if (movies.isEmpty()) resultOfLoad.value = Common.LOAD_STATE_NOTHING
                    else resultOfLoad.value = Common.LOAD_STATE_SOMETHING
                }

                override fun onFailure(call: Call<Discover>, t: Throwable) {
                    Log.d("ErRoR", "onFailureDiscover: " + t.message)
                }
            }
        )
    } catch (e: Exception) {
        Log.d("POKEMON", e.toString())
        //letShowDialog.value = true
    }
}

@Composable
fun ShowErrorDialog(letShowDialog: MutableState<Boolean>) {

    if (letShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Alert Dialog")
            },
            text = {
                Text("JetPack Compose Alert Dialog!")
            },
            confirmButton = {
                Button(onClick = {
                    letShowDialog.value = false
                }) {
                    Text("Ок")
                }
            }
        )
    }
}

// список фильмов
@Composable
fun MovieListElem(state: MutableState<MutableList<Result>>, navController: NavHostController, resultOfLoad: MutableState<Int> ) {
    var movies = state.value

    if (resultOfLoad.value == Common.LOAD_STATE_NOTHING) {
        Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "По Вашему запросу ничего не найдено :(",
                modifier = Modifier.padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    if (movies.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(movies) { index, movie ->
                MovieCardElem(movie = movie, navController = navController)
                if (index < movies.size - 1) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Divider(color = SearchLineColorStart, thickness = 1.dp)
                }
            }
        }
    }
}

// строка поиска
@Composable
fun SearchFieldElem(state: MutableState<MutableList<Result>>, resultOfLoad: MutableState<Int>) {
    var searchLineState = remember { mutableStateOf("") }
    var letShowErrorDialog =  remember { mutableStateOf(false) }

    ShowErrorDialog(letShowErrorDialog)

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
//            keyboardActions = KeyboardActions(onDone = {
//                //updateListMovies(searchLineState.value) ----------------------- ФИЛЬТРАЦИЯ
//                if (searchLineState.value.trim() != "") {
//                    getMySearchDiscover(request = searchLineState.value, state = state, letShowDialog = letShowErrorDialog)
//                }
////                else {
////                    getMyDiscover(state = state)
////                }
//            }),
            value = searchLineState.value,
            onValueChange = {
                searchLineState.value = it
                if (searchLineState.value.trim() == "")  { getMyDiscover(state = state, resultOfLoad = resultOfLoad) }
                else { getMySearchDiscover(request = searchLineState.value, state = state, letShowDialog = letShowErrorDialog, resultOfLoad = resultOfLoad) }
            },
            singleLine = true,
            textStyle = TextStyle(color = SearchLineColorEnd, fontSize = 20.sp),
            placeholder = { Text(text = "Введите название фильма", color = HintColor) },
            trailingIcon = {
                IconButton(onClick = {
                    if (searchLineState.value.trim() != "") {
                        getMySearchDiscover(request = searchLineState.value, state = state, letShowDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)
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
fun MovieCardElem(movie: Result, navController: NavHostController) {
        // установка формата даты
    Log.d("POKEMON", "${movie.title}: ${movie.release_date.toString()}")
    val date = movie.release_date.formatDate()


    Log.d("PATH", "${movie.title}: ${movie.poster_path}")

    Row (modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth()
        .clickable {
            navController.navigate("detailMoviesView/${movie.id}")
        }
    ) {
            // картинка фильма
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                //painter = painterResource(id = R.drawable.default_image),
                painter = if (movie.poster_path != null) { rememberImagePainter("${Common.BASE_URL_IMAGES}${Common.POSTER_SIZE_LIST}${movie.poster_path}") }
                else { painterResource(R.drawable.default_image) },
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

        //var isExpanded by remember { mutableStateOf(false) }

//        val surfaceColor: Color by animateColorAsState(
//            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface
//        )
            // информация о фильме
        Column () { //modifier = Modifier.clickable { isExpanded = !isExpanded }
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
                //color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = "Дата релиза: $date",
                    modifier = Modifier.padding(all = 4.dp),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                    //maxLines = if (isExpanded) Int.MAX_VALUE else 1
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