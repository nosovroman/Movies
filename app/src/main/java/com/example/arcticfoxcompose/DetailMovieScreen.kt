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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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

    getDetailMovie(movieId = movieId, detailState = detailState)
    getGallery(movieId = movieId, posterState = posterState)
    getReview(movieId = movieId, reviewState = reviewState)
    getActors(movieId = movieId, actorState = actorState)

    val scrollState = rememberScrollState()
    Column (
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        GalleryElem(posterState)
        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            MovieInfo(detailState)
            DescriptionReview(detailState = detailState, reviewState = reviewState)
            ActorsInfo(actorState)
            YoutubeButton(detailState)
        }

    }
}

@Composable
fun GalleryElem(posterState: MutableState<MutableList<Poster>>) {
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
fun MovieInfo(detailState: MutableState<NeedDetailMovie>) {
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

        Spacer(modifier = Modifier.height(spaceInfo))
        Spacer(modifier = Modifier.height(spaceInfo))
    }
}

@Composable
fun DescriptionReview(detailState: MutableState<NeedDetailMovie>, reviewState: MutableState<MutableMap<String, String>>) {
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
            if (reviewState.value["author"] != "") {
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
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ActorsInfo(actorState: MutableState<MutableList<String>>) {
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
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


@Composable
fun YoutubeButton(detailState: MutableState<NeedDetailMovie>) {
    //val detailMovie = detailState.
    // кнопка видео Ютуб
    //if (detailMovie.video) {
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            val context = LocalContext.current
            val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/")) }
            Button(onClick = { context.startActivity(intent) }) {
                Text(text = "Смотреть видео")
            }
        }
    //}
}

fun getGallery(movieId: Int, posterState: MutableState<MutableList<Poster>>) {
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
            }
        }
    )
}

fun getDetailMovie(movieId: Int, detailState: MutableState<NeedDetailMovie>) {
    Common.retrofitService.getDetailMovie(movieId).enqueue(
        object : Callback<DetailMovie> {
            override fun onResponse(call: Call<DetailMovie>, response: Response<DetailMovie>) {
                //val responseBody = response.body()!!.posters
                val responseBody = response.body()
                val genres = responseBody?.genres?.map { it.name } ?: listOf()
                val title = responseBody?.title ?: ""
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
            }

            override fun onFailure(call: Call<DetailMovie>, t: Throwable) {
                Log.d("ErRoR", "onFailureDiscover: " + t.message)
            }
        }
    )
}

fun getReview(movieId: Int, reviewState: MutableState<MutableMap<String, String>>) {
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
            }
        }
    )
}

fun getActors(movieId: Int, actorState: MutableState<MutableList<String>>) {
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
            }
        }
    )
}