package com.example.arcticfoxcompose

import com.example.arcticfoxcompose.dataClasses.Message

object SampleData1 {
    var conversationSample = mutableListOf(
        Message(
            "Colleague",
            "Test...Test...Test..."
        ),
        Message(
            "Colleague",
            "List of Android versions:\n" +
                    "Android Pie (API 28)\n" +
                    "Android 10 (API 29)\n" +
                    "Android 11 (API 30)\n" +
                    "Android 12 (API 31)\n"
        ),
//        Message(
//            "Colleague",
//            "I think Kotlin is my favorite programming language.\n" +
//                    "It's so much fun!"
//        ),
//        Message(
//            "Colleague",
//            "Searching for alternatives to XML layouts..."
//        ),
        Message(
            "Colleague",
            "Hey, take a look at Jetpack Compose, it's great!\n" +
                    "It's the Android's modern toolkit for building native UI." +
                    "It simplifies and accelerates UI development on Android." +
                    "Less code, powerful tools, and intuitive Kotlin APIs :)"
        ),
    )
}