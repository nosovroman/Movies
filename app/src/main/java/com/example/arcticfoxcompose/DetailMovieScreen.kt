package com.example.arcticfoxcompose

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.common.formatDate
import com.example.arcticfoxcompose.dataClasses.Actors.Actors
import com.example.arcticfoxcompose.dataClasses.DetailMovie.DetailMovie
import com.example.arcticfoxcompose.dataClasses.DetailMovie.NeedDetailMovie
import com.example.arcticfoxcompose.dataClasses.Gallery
import com.example.arcticfoxcompose.dataClasses.Poster
import com.example.arcticfoxcompose.dataClasses.Review.Review
import com.example.arcticfoxcompose.dataClasses.Video.ResVideo
import com.example.arcticfoxcompose.dataClasses.Video.Video
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DetailMovieScreen(movieId: Int) {
    var posterState = remember { mutableStateOf(mutableListOf<Poster>()) }
    var detailState = remember { mutableStateOf(NeedDetailMovie()) }
    var reviewState = remember { mutableStateOf(mutableMapOf<String, String>()) }
    var actorState = remember { mutableStateOf(mutableListOf<String>()) }
    var videoState = remember { mutableStateOf(mutableListOf<ResVideo>()) }

    var resultOfLoad = remember { mutableStateOf(Common.LOAD_STATE_NOTHING) }
    var letShowErrorDialog =  remember { mutableStateOf("") }

    getGallery(movieId = movieId, posterState = posterState, letShowErrorDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)
    getDetailMovie(movieId = movieId, detailState = detailState, letShowErrorDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)
    getReview(movieId = movieId, reviewState = reviewState, letShowErrorDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)
    getActors(movieId = movieId, actorState = actorState, letShowErrorDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)
    getVideo(movieId = movieId, videoState = videoState, letShowErrorDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)

    ShowErrorDialog(movieId = movieId,
        posterState = posterState,
        detailState = detailState,
        reviewState = reviewState,
        actorState = actorState,
        videoState = videoState, letShowDialog = letShowErrorDialog, resultOfLoad = resultOfLoad)


    val scrollState = rememberScrollState()
    Column (
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        GalleryElem(posterState, resultOfLoad = resultOfLoad, letShowErrorDialog = letShowErrorDialog)
        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            if ( resultOfLoad.value == Common.LOAD_STATE_SOMETHING ){
                MovieInfo(detailState, resultOfLoad = resultOfLoad, letShowErrorDialog = letShowErrorDialog)
                Spacer(modifier = Modifier.height(10.dp))
                DescriptionReview(detailState = detailState, reviewState = reviewState, resultOfLoad = resultOfLoad, letShowErrorDialog = letShowErrorDialog)
                Spacer(modifier = Modifier.height(10.dp))
                ActorsInfo(actorState, resultOfLoad = resultOfLoad, letShowErrorDialog = letShowErrorDialog)
                Spacer(modifier = Modifier.height(10.dp))
                YoutubeButton(videoState, resultOfLoad = resultOfLoad, letShowErrorDialog = letShowErrorDialog)
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onSurface)
                }
            }
        }
    }
}

@Composable
private fun ShowErrorDialog(movieId: Int,
                            posterState: MutableState<MutableList<Poster>>,
                            detailState: MutableState<NeedDetailMovie>,
                            reviewState: MutableState<MutableMap<String, String>>,
                            actorState: MutableState<MutableList<String>>,
                            videoState: MutableState<MutableList<ResVideo>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>) {

    if (letShowDialog.value != "") {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "Ошибка!")
            },
            text = {
                Text("$letShowDialog")
            },
            confirmButton = {
                Button(onClick = {
                    letShowDialog.value = ""
                    updateDetailScreen(movieId = movieId,
                        posterState = posterState,
                        detailState = detailState,
                        reviewState = reviewState,
                        actorState = actorState,
                        videoState = videoState, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad)
                }) {
                    Text("Обновить")
                }
            }
        )
    }
}

@Composable
fun GalleryElem(posterState: MutableState<MutableList<Poster>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val posters = posterState.value
    Log.d("privet", posters.toString())
        if (posters.isNotEmpty()) {
            LazyRow () {
                itemsIndexed(posters) { index, item ->
                    Image(
                        painter = rememberImagePainter("${Common.BASE_URL_IMAGES}${Common.POSTER_SIZE_MOVIE}${item.file_path}"),
                        contentDescription = "Image Text",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(180.dp)
                            .height(255.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                2.5.dp,
                                MaterialTheme.colors.secondary,
                                shape = RoundedCornerShape(15.dp)
                            ),
                    )
                    if (index < posters.size - 1) {
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
            }
        }
//        else {
//            Image(
//                painter = painterResource(R.drawable.default_image),
//                contentDescription = "Image Text",
//                contentScale = ContentScale.FillWidth,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
//            )
//        }
}

@Composable
fun MovieInfo(detailState: MutableState<NeedDetailMovie>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val detailMovie = detailState.value

    val genreString = detailMovie.genres.toString().drop(1).dropLast(1)
    val date = detailMovie.release_date.formatDate()

    val spaceInfo = 5.dp
    val typeInfoColor = MaterialTheme.colors.primary
    val infoColor = MaterialTheme.colors.onSecondary

    Column() {
        Spacer(modifier = Modifier.height(spaceInfo))
        Text(
            text = detailMovie.title,
            color = typeInfoColor,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(spaceInfo))

            // жанр
        Row() {
            Text(
                text = "Жанр: ",
                color = typeInfoColor,
                fontSize = 16.sp,
            )
            Text(
                text = genreString,
                color = infoColor,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(spaceInfo))

            // дата релиза
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Дата релиза: ",
                color = typeInfoColor,
                fontSize = 16.sp
            )

            Text(
                text = date,
                color = infoColor,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(spaceInfo))

            // рейтинг
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Рейтинг:\t",
                color = typeInfoColor,
                fontSize = 16.sp
            )
            Text(
                text = "${detailMovie.vote_average} ⭐",
                color = infoColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DescriptionReview(detailState: MutableState<NeedDetailMovie>, reviewState: MutableState<MutableMap<String, String>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val detailMovie = detailState.value
    val descriptionMode = remember { mutableStateOf(true) }
    val typeInfoColor = MaterialTheme.colors.primary
    val infoColor = MaterialTheme.colors.onSecondary
    val activeColor = MaterialTheme.colors.primary

        // кнопки описание и рецензия
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (descriptionMode.value) activeColor else Color.Transparent,
                contentColor = if (descriptionMode.value) MaterialTheme.colors.background else activeColor
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            modifier = Modifier.border(width = 2.5.dp, color = if (descriptionMode.value) activeColor else Color.Transparent, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)),
            onClick = {
                descriptionMode.value = true
            }
        ) {
            Text("Описание")
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (!descriptionMode.value) activeColor else Color.Transparent,
                contentColor = if (!descriptionMode.value) MaterialTheme.colors.background else activeColor,
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            modifier = Modifier.border(width = 2.5.dp, color = if (!descriptionMode.value) activeColor else Color.Transparent, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)),
            onClick = {
                descriptionMode.value = false
            }
        ) {
            Text("Рецензия")
        }
    }

    // поле с описанием или рецензией
    Text(
        text = if (descriptionMode.value)
        {detailMovie.overview}
        else {
            if (reviewState.value["author"] != "" && reviewState.value["author"] != null) {
                "[${reviewState.value["author"]}]: ${reviewState.value["content"]}"
            } else { "Рецензий пока не было" }

        },
        modifier = Modifier
            .border(width = 1.5.dp, color = typeInfoColor, RoundedCornerShape(10.dp))
            .padding(all = 10.dp)
            .fillMaxWidth(),
        color = infoColor,
        maxLines = Int.MAX_VALUE
    )
}

@Composable
fun ActorsInfo(actorState: MutableState<MutableList<String>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    // поле с актерами
    val typeInfoColor = MaterialTheme.colors.primary
    val infoColor = MaterialTheme.colors.onSecondary
    val expandedMode = remember { mutableStateOf(false) }
    val activeColor = MaterialTheme.colors.primary

    if (actorState.value.isNotEmpty()) {
        Column() {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (expandedMode.value) activeColor else Color.Transparent,
                        contentColor = if (expandedMode.value) MaterialTheme.colors.background else activeColor,
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        //.padding(start = 10.dp)
                        .border(width = 1.5.dp,
                            color = activeColor,
                            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                        ),
                    onClick = {
                        expandedMode.value = !expandedMode.value
                    }
                ) {
                    Text("Актеры")
                }
            }

            Text(
                text = actorState.value.toString().drop(1).dropLast(1),
                modifier = Modifier
                    .border(width = 1.5.dp, color = typeInfoColor, RoundedCornerShape(10.dp))
                    .padding(all = 10.dp)
                    .fillMaxWidth(),
                color = infoColor,
                maxLines = if (expandedMode.value) Int.MAX_VALUE else 3
            )
        }
    }
}

@Composable
fun YoutubeButton(videoState: MutableState<MutableList<ResVideo>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    //val detailMovie = detailState.
    // кнопка видео Ютуб
    if (videoState.value.isNotEmpty()) {
        val keyVideo = videoState.value[0].key
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            val context = LocalContext.current
            val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(Common.BASE_URL_YOUTUBE + keyVideo)) }
            Button(onClick = { context.startActivity(intent) }) {
                Text(text = "Смотреть видео")
            }
        }
    }
}

fun getGallery(movieId: Int, posterState: MutableState<MutableList<Poster>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val movieInfo = mutableListOf<Poster>()

    Common.retrofitService.getGallery(movieId).enqueue(
        object : Callback<Gallery> {
            override fun onResponse(call: Call<Gallery>, response: Response<Gallery>) {
                val responseBody = response.body()!!.posters
                val myStringBuilder = StringBuilder()
                for (myData in responseBody) {
                    myStringBuilder.append("${myData.file_path}\n")
                    movieInfo.add(myData)
                }
                Log.d("MyMovie", "END: \n $myStringBuilder")
                posterState.value = movieInfo
            }

            override fun onFailure(call: Call<Gallery>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: " + t.message)
                letShowErrorDialog.value = t.message.toString()
            }
        }
    )
}

fun getDetailMovie(movieId: Int, detailState: MutableState<NeedDetailMovie>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    Common.retrofitService.getDetailMovie(movieId).enqueue(
        object : Callback<DetailMovie> {
            override fun onResponse(call: Call<DetailMovie>, response: Response<DetailMovie>) {
                //val responseBody = response.body()!!.posters
                val responseBody = response.body()
                val genres = responseBody?.genres?.map { it.name } ?: listOf()
                val title = responseBody?.title ?: "null"
                val release_date = responseBody?.release_date ?: ""
                val vote_average = responseBody?.vote_average ?: 0.0
                val video = responseBody?.video ?: false
                val overview = responseBody?.overview ?: "Описание отсутствует"

                val needDetailMovie = NeedDetailMovie(
                    genres = genres,
                    title = title,
                    release_date = release_date,
                    vote_average = vote_average,
                    video = video,
                    overview = overview
                )

                //Log.d("MyDetail", "END: \n $needDetailMovie")
                detailState.value = needDetailMovie
                Log.d("MyDetail", "END: \n ${detailState.value}")

                if (needDetailMovie.title == "null") resultOfLoad.value = Common.LOAD_STATE_NOTHING
                else resultOfLoad.value = Common.LOAD_STATE_SOMETHING
            }

            override fun onFailure(call: Call<DetailMovie>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: " + t.message)
                letShowErrorDialog.value = t.message.toString()
            }
        }
    )
}

fun getReview(movieId: Int, reviewState: MutableState<MutableMap<String, String>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    var reviewInfo = mutableMapOf<String, String>()

    Common.retrofitService.getReview(movieId).enqueue(
        object : Callback<Review> {
            override fun onResponse(call: Call<Review>, response: Response<Review>) {
                val responseBody = response.body()
                var author: String
                var content: String

                if (responseBody?.results?.isNotEmpty() == true) {
                    author = responseBody.results[0].author.toString()
                    content = responseBody.results[0].content.toString()
                }
                else {
                    author = ""
                    content = ""
                }

                reviewInfo["author"] = author
                reviewInfo["content"] = content

                reviewState.value = reviewInfo
            }

            override fun onFailure(call: Call<Review>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: " + t.message)
                letShowErrorDialog.value = t.message.toString()
            }
        }
    )
}

fun getActors(movieId: Int, actorState: MutableState<MutableList<String>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val listActors = mutableListOf<String>()

    Common.retrofitService.getActors(movieId).enqueue(
        object : Callback<Actors> {
            override fun onResponse(call: Call<Actors>, response: Response<Actors>) {
                //val responseBody = response.body()!!.posters
                val responseBody = response.body()
                val actors = responseBody!!.cast

                for (actor in actors) {
                    if (actor.known_for_department == "Acting") {
                        listActors.add(actor.name)
                    }
                }

                actorState.value = listActors
                Log.d("MyDetail", "END: \n ${actorState.value}")
            }

            override fun onFailure(call: Call<Actors>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: " + t.message)
                letShowErrorDialog.value = t.message.toString()
            }
        }
    )
}

fun getVideo(movieId: Int, videoState: MutableState<MutableList<ResVideo>>, resultOfLoad: MutableState<Int>, letShowErrorDialog: MutableState<String>) {
    val videos = mutableListOf<ResVideo>()

    Common.retrofitService.getVideo(movieId).enqueue(
        object : Callback<Video> {
            override fun onResponse(call: Call<Video>, response: Response<Video>) {
                val responseBody = response.body()!!.results
                val myStringBuilder = StringBuilder()
                for (myData in responseBody) {
                    myStringBuilder.append("${myData}\n")
                    videos.add(myData)
                }
                Log.d("MyVideo", "END: \n $myStringBuilder")
                videoState.value = videos
            }

            override fun onFailure(call: Call<Video>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: "+ t.message)
                letShowErrorDialog.value = t.message.toString()
            }
        }
    )
}

// обновление экрана
fun updateDetailScreen(movieId: Int,
                       posterState: MutableState<MutableList<Poster>>,
                       detailState: MutableState<NeedDetailMovie>,
                       reviewState: MutableState<MutableMap<String, String>>,
                       actorState: MutableState<MutableList<String>>,
                       videoState: MutableState<MutableList<ResVideo>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>) {

    getGallery(movieId = movieId, posterState = posterState, letShowErrorDialog = letShowDialog, resultOfLoad = resultOfLoad)
    getDetailMovie(movieId = movieId, detailState = detailState, letShowErrorDialog = letShowDialog, resultOfLoad = resultOfLoad)
    getReview(movieId = movieId, reviewState = reviewState, letShowErrorDialog = letShowDialog, resultOfLoad = resultOfLoad)
    getActors(movieId = movieId, actorState = actorState, letShowErrorDialog = letShowDialog, resultOfLoad = resultOfLoad)
    getVideo(movieId = movieId, videoState = videoState, letShowErrorDialog = letShowDialog, resultOfLoad = resultOfLoad)

//    if (request == "") {
//        getMyDiscover(state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad)
//    } else {
//        getMySearchDiscover(state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad, request = request)
//    }
}