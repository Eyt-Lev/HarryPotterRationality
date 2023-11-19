package com.eyt.harrypotter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eyt.harrypotter.model.Chapter
import com.eyt.harrypotter.utils.ReadFilesUtils.getChapterTitle

private const val lineHeightMultiplier = 1.15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterPage(
    chapter: Chapter,
    fontSize: Int,
    showSettings: () -> Unit,
    showBottomBar: Boolean,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lazyColumnState = rememberLazyListState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "פרק " + (chapter.chapterNumber?.toString() ?: "") + " - " + chapter.title
                    )
                },
                navigationIcon = {
                    IconButton( onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            "חזרה"
                        )
                    }
                },
                actions = {
                    if (!showBottomBar){
                        var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
                        when {
                            chapter.chapterNumber == null -> Unit
                            lazyColumnState.canScrollForward -> IconButton(
                                onClick = { navController.navigate("Chapter/${chapter.chapterNumber + 1}") }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowRight,
                                    "פרק " + (chapter.chapterNumber + 1) + " - " + context.getChapterTitle(
                                        chapter.chapterNumber + 1
                                    )
                                )
                            }
                            else -> FilledIconButton(
                                onClick = { navController.navigate("Chapter/${chapter.chapterNumber + 1}") }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowRight,
                                    "פרק " + (chapter.chapterNumber + 1) + " - " + context.getChapterTitle(
                                        chapter.chapterNumber + 1
                                    )
                                )
                            }
                        }
                        Box {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    "עוד"
                                )
                            }
                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("הגדרות") },
                                    onClick = {
                                        showSettings()
                                        isMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            "הגדרות"
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (showBottomBar) BottomAppBar(
                actions = {
                    IconButton(onClick = showSettings) {
                        Icon(
                            Icons.Default.Settings,
                            "הגדרות"
                        )
                    }
                },
                floatingActionButton = if (chapter.chapterNumber != null) {
                    {
                        ExtendedFloatingActionButton(
                            onClick = { navController.navigate("Chapter/${chapter.chapterNumber + 1}") },
                            expanded = !lazyColumnState.canScrollForward,
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowRight,
                                    "פרק " + (chapter.chapterNumber + 1) + " - " + context.getChapterTitle(
                                        chapter.chapterNumber + 1
                                    )
                                )
                            },
                            text = {
                                Text(
                                    "פרק " + (chapter.chapterNumber + 1) + " - " + context.getChapterTitle(
                                        chapter.chapterNumber + 1
                                    )
                                )
                            }
                        )
                    }
                } else null
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(it),
            contentPadding = PaddingValues(horizontal = 6.dp),
            state = lazyColumnState
        ) {
//            item {
//                Text(
//                    chapter.title,
//                    style = MaterialTheme.typography.headlineLarge,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    textAlign = TextAlign.Center
//                )
//            }
            chapter.quotes?.let {
                items(chapter.quotes) {
                    Text(
                        it,
                        fontSize = (fontSize * 0.8).sp,
                        lineHeight = (fontSize * 0.8 * lineHeightMultiplier).sp
                    )
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    )
                }
            }
            chapter.content?.let {
                items(chapter.content) {
                    Text(
                        it,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * lineHeightMultiplier).sp
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
//            chapterFooter(
//                chapterBook = chapter.book,
//                chapterNumber = chapter.chapterNumber,
//                customContinueTo = chapter.customContinueTo,
//                navController = navController
//            )
        }

    }
}