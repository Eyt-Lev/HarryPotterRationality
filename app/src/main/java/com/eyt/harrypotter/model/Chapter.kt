package com.eyt.harrypotter.model

data class Chapter(
    val title: String,
    val quotes: List<String>?,
    val chapterNumber: Int?,
    val content: List<String>?,
    val customContinueTo: Int?
)
