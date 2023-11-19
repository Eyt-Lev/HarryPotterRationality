package com.eyt.harrypotter.utils

import android.content.Context
import com.eyt.harrypotter.model.Chapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


object ReadFilesUtils {
    fun Context.openHarryChapter(
        chapterNumber: Int?
    ): Chapter? {
        if (chapterNumber == null) return null
        val path = "chapter_$chapterNumber.json"
        val stringJson =  readAssetsFile(path) ?: return null
        val chapter = try {
            Gson().fromJson(stringJson, Chapter::class.java)
        } catch (_:JsonSyntaxException){
            null
        } ?: return null
        return chapter.copy(
            chapterNumber = chapterNumber,
            content = chapter.content?.map { it.trim() }
        )
    }

    fun Context.getChapterTitle(
        chapterNumber: Int?
    ): String? {
        if (chapterNumber == null) return null
        val path = "chapter_$chapterNumber.json"
        val stringJson = getChapterTitle(path) ?: return null
        return try {
            Gson().fromJson(stringJson, Chapter::class.java).title
        } catch (_:JsonSyntaxException){
            null
        }
    }

    private fun Context.getChapterTitle(
        path: String
    ): String? {
        var reader: BufferedReader? = null
        return try {
            val output = StringBuilder()
            reader = BufferedReader(
                InputStreamReader(
                    /* in = */ assets.open(path),
                    /* charsetName = */ "UTF8"
                )
            )
            var line = 0
            while (reader.ready() && line < 2){
                output.append(reader.readLine())
                line++
            }
            return output.removeSuffix(",").toString().plus("}")
        } catch (_: FileNotFoundException) {
            //File not found
            null
        } catch (_: IOException) {
            null
        } finally {
            if (reader != null) {
                try { reader.close() } catch (_: IOException) { }
            }
        }
    }

    private fun Context.readAssetsFile(path: String): String? {
        var reader: BufferedReader? = null
        return try {
            val output = StringBuilder()
            reader = BufferedReader(
                InputStreamReader(
                    /* in = */ assets.open(path),
                    /* charsetName = */ "UTF8"
                )
            )
            while (reader.ready()){
                output.append(reader.readLine())
            }
            return output.toString()
        } catch (_: FileNotFoundException) {
            //File not found
            null
        } catch (_: IOException) {
            null
        } finally {
            if (reader != null) {
                try { reader.close() } catch (_: IOException) { }
            }
        }

    }
}