package com.example.arcticfoxcompose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.arcticfoxcompose.common.Common
import com.example.arcticfoxcompose.dataClasses.Gallery
import com.example.arcticfoxcompose.dataClasses.Poster
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DetailMovieScreen(movieId: Int) {
    var posterState = remember { mutableStateOf(mutableListOf<Poster>()) }

    loadMovieInfo(movieId = movieId, posterState = posterState)

    //Box(modifier = Modifier
    //    .fillMaxSize()
    //    .background(color = Color.White)) {
        Column () {
//            Spacer(modifier = Modifier.height(10.dp))
            GalleryElem(posterState)
            //MovieInfo()
        }
    //}
}

fun loadMovieInfo(movieId: Int, posterState: MutableState<MutableList<Poster>>) {
    getGallery(movieId = movieId, posterState = posterState)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GalleryElem(posterState: MutableState<MutableList<Poster>>) {
    val posters = posterState.value
        if (posters.isNotEmpty()) {

//                HorizontalPager(count = posters.count()) { index ->
//                    Image(
//                        painter = rememberImagePainter("${Common.BASE_URL_IMAGES}${Common.POSTER_SIZE_MOVIE}${posters[index].file_path}"),
//                        contentDescription = "Image Text",
//                        contentScale = ContentScale.FillWidth,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(10.dp))
//                            .border(2.5.dp, color = Color.Yellow)
//                    )
//                }

            LazyRow (modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(items = posters) { index, item ->
                    Image(
                        painter = rememberImagePainter("${Common.BASE_URL_IMAGES}${Common.POSTER_SIZE_MOVIE}${item.file_path}"),
                        contentDescription = "Image Text",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            //.fillMaxWidth()
                            //.width(120.dp).height(170.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.5.dp, color = MaterialTheme.colors.background, shape = RoundedCornerShape(10.dp))
                    )
                }
            }
        }
        else {
            Image(
                painter = painterResource(R.drawable.default_image),
                contentDescription = "Image Text",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            )
        }
}


@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun PreviewConversation() {
    Box(modifier = Modifier.background(color = Color.Gray), contentAlignment = Alignment.TopStart) {
        HorizontalPager(count = 1, modifier = Modifier.align(Alignment.TopStart)) { index ->
            Image(
                painter = painterResource(R.drawable.default_image),
                contentDescription = "Image Text",
                //contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(2.5.dp, color = Color.Yellow)
            )
        }
    }
}

fun getGallery(movieId: Int, posterState: MutableState<MutableList<Poster>>) {
    val movieInfo = mutableListOf<Poster>()

    Common.retrofitService.getGallery(movieId).enqueue(
        object : Callback<Gallery> {
            override fun onResponse(call: Call<Gallery>, response: Response<Gallery>) {
//                Log.d("MyDiscover", "Hello bro")
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