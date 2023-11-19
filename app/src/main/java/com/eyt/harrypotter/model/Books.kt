package com.eyt.harrypotter.model

enum class Books(
    val bookName: String? = null,
    val numberOfChapters: IntRange
) {
    BOOK_1(
        bookName = "הארי פוטר והשיטה הרציונלית",
        numberOfChapters = 1..21
    ),
    BOOK_2(
        bookName = "הארי פוטר ומשחקי הפרופסור",
        numberOfChapters = 22..25
    )
}