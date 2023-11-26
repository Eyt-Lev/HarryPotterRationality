package com.eyt.harrypotter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eyt.harrypotter.model.Books
import com.eyt.harrypotter.model.Chapter
import com.eyt.harrypotter.ui.theme.HarryPotterTheme
import com.eyt.harrypotter.utils.ReadFilesUtils.getChapterTitle
import com.eyt.harrypotter.utils.ReadFilesUtils.openHarryChapter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val FONT_SIZE = intPreferencesKey("font_size")
val SHOW_BOTTOM_BAR = booleanPreferencesKey("show_bottom_bar")
const val INITIAL_FONT_SIZE = 16
const val INITIAL_SHOW_BOTTOM_BAR = true

class MainActivity : ComponentActivity() {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fontSizeFlow = dataStore.data.map { preferences ->
            preferences[FONT_SIZE] ?: INITIAL_FONT_SIZE
        }
        val showBottomBarFlow = dataStore.data.map { preferences ->
            preferences[SHOW_BOTTOM_BAR] ?: INITIAL_SHOW_BOTTOM_BAR
        }

        setContent {
            val fontSize by fontSizeFlow.collectAsState(INITIAL_FONT_SIZE)
            val showBottomBar by showBottomBarFlow.collectAsState(INITIAL_SHOW_BOTTOM_BAR)
            val navController = rememberNavController()
            HarryPotterTheme {
                Surface(
                     modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "MainScreen" //TODO: Better names in const
                    ){
                        composable("MainScreen"){
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp, 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ){
                                items(Books.entries){
                                    Card(
                                        onClick = {
                                            navController.navigate("Book/${it.ordinal}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 48.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text((it.ordinal + 1).toString())
                                            Spacer(modifier = Modifier.width(8.dp))
                                            it.bookName?.let { Text(it) }
                                        }
                                    }
                                }
                            }
                        }
                        composable("Book/{book}"){ backStackEntry ->
                            var chapters by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
                            val bookNumber = backStackEntry.arguments?.getString("book")?.toIntOrNull() ?: return@composable
                            val book = Books.entries.getOrNull(bookNumber) ?: return@composable
                            val context = LocalContext.current
                            LaunchedEffect(chapters){
                                if (chapters.isEmpty()) chapters =
                                    (book.numberOfChapters).associateBy(
                                        keySelector = { it },
                                        valueTransform = { context.getChapterTitle(it) ?: "" }
                                    )
                            }
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp, 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ){
                                items(chapters.filter { it.value.isNotEmpty() }.toList()){
                                    Card(
                                        onClick = { navController.navigate("Chapter/${it.first}") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 48.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "פרק " + it.first + " - " + it.second,
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        composable("Chapter/{chapter}"){ backStackEntry ->
                            var chapter by remember { mutableStateOf<Chapter?>(null) }
                            var noChapter by remember { mutableStateOf(false) }
                            var showSettings by rememberSaveable { mutableStateOf(false) }
                            LaunchedEffect(Unit){
                                chapter = runBlocking {
                                    openHarryChapter(
                                        chapterNumber = backStackEntry.arguments?.getString("chapter")?.toIntOrNull()
                                    ).also {
                                        noChapter = it == null
                                    }
                                }
                            }
                            if (noChapter) Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("הפרק לא נמצא")
                                Button(onClick = { navController.popBackStack() }) {
                                    Text("חזרה למסך הבית")
                                }
                            }
                            chapter?.let { ChapterPage(
                                chapter = it,
                                fontSize = fontSize,
                                showSettings = { showSettings = !showSettings },
                                showBottomBar = showBottomBar,
                                navController = navController
                            ) }
                            if (showSettings) PreferencesSheet(
                                onDismiss = { showSettings = false },
                                fontSize = fontSize,
                                updateFontSize = ::updateFontSize,
                                showBottomBar = showBottomBar,
                                updateShowBottomBar = ::updateShowBottomBar
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateFontSize(newSize: Int){
        lifecycleScope.launch {
            dataStore.edit { settings ->
                settings[FONT_SIZE] = newSize
            }
        }
    }

    private fun updateShowBottomBar(newValue: Boolean){
        lifecycleScope.launch {
            dataStore.edit { settings ->
                settings[SHOW_BOTTOM_BAR] = newValue
            }
        }
    }
}

