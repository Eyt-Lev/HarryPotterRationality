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
        numberOfChapters = 22..37
    ),
    BOOK_3(
        bookName = "הארי פוטר וצללי המוות",
        numberOfChapters = 38..64
    ),
    BOOK_4(
        bookName = "הרמיוני גרינג'ר וקריאת עוף החול",
        numberOfChapters = 65..85
    ),
    BOOK_5(
        bookName = "הארי פוטר והאויב האחרון",
        numberOfChapters = 86..99
    ),
    BOOK_6(
        bookName = "הארי פוטר ואבן החכמים",
        numberOfChapters = 100..122
    ),
}