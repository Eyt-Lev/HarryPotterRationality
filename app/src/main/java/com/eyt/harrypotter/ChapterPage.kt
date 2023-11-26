package com.eyt.harrypotter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eyt.harrypotter.model.Books
import com.eyt.harrypotter.model.Chapter
import com.eyt.harrypotter.ui.theme.HarryPotterTheme
import com.eyt.harrypotter.utils.ReadFilesUtils.getChapterTitle
import com.eyt.harrypotter.utils.ReadFilesUtils.openHarryChapter

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
            chapter.chapterNumber?.let { chapterNumber ->
                ChapterTopBar(
                    chapterNumber = chapterNumber,
                    chapterTitle = chapter.title,
                    onBack = { navController.popBackStack() },
                    showBottomBar = showBottomBar,
                    scrollBehavior = scrollBehavior,
                    canScrollForward = lazyColumnState.canScrollForward,
                    navigateToChapter = {
                        navController.navigate("Chapter/$it"){
                            popUpTo("Chapter/${chapter.chapterNumber}")
                        }
                    },
                    showSettings = showSettings
                )
            }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if (chapter.chapterNumber > Books.BOOK_1.numberOfChapters.first) {
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    navController.navigate("Chapter/${chapter.chapterNumber - 1}"){
                                        popUpTo("Chapter/${chapter.chapterNumber}")
                                    }
                                }){
                                    Icon(
                                        Icons.AutoMirrored.Default.ArrowLeft,
                                        "הפרק הקודם"
                                    )
                                }
                            }
                            ExtendedFloatingActionButton(
                                onClick = {
                                    navController.navigate("Chapter/${chapter.chapterNumber + 1}") {
                                        popUpTo("Chapter/${chapter.chapterNumber}")
                                    }
                                },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterTopBar(
    chapterNumber: Int,
    chapterTitle: String,
    onBack: () -> Unit,
    showBottomBar: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    canScrollForward: Boolean,
    navigateToChapter: (Int) -> Unit,
    showSettings: () -> Unit
) {
    LargeTopAppBar(
        title = {
            Text(
                "פרק $chapterNumber - $chapterTitle"
            )
        },
        navigationIcon = {
            IconButton( onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    "חזרה"
                )
            }
        },
        actions = {
            if (!showBottomBar) TopBarWithoutBottomBarActions(
                chapterNumber = chapterNumber,
                canScrollForward = canScrollForward,
                navigateToChapter = navigateToChapter,
                showSettings = showSettings
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun TopBarWithoutBottomBarActions(
    chapterNumber: Int,
    canScrollForward: Boolean,
    navigateToChapter: (Int) -> Unit,
    showSettings: () -> Unit
) {
    val context = LocalContext.current
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val nextChapter = chapterNumber + 1

    when {
        nextChapter > Books.BOOK_6.numberOfChapters.last -> Unit
        canScrollForward -> IconButton(onClick = { navigateToChapter(nextChapter) }) {
            Icon(
                Icons.AutoMirrored.Default.ArrowRight,
                "פרק " + nextChapter + " - " + context.getChapterTitle(nextChapter)
            )
        }
        else -> FilledIconButton(onClick = { navigateToChapter(nextChapter) }) {
            Icon(
                Icons.AutoMirrored.Default.ArrowRight,
                "פרק " + nextChapter + " - " + context.getChapterTitle(nextChapter)
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
                    isMenuExpanded = false
                    showSettings()
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Settings,
                        "הגדרות"
                    )
                }
            )
            if (chapterNumber > Books.BOOK_1.numberOfChapters.first) DropdownMenuItem(
                text = { Text("הפרק הקודם") },
                onClick = {
                    isMenuExpanded = false
                    navigateToChapter(chapterNumber - 1)
                },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowLeft,
                        "הפרק הקודם"
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun ChapterPreview() {
    val chapterNumber = 11
    HarryPotterTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            LocalContext.current.openHarryChapter(
                chapterNumber
            )?.let {
                ChapterPage(
                    chapter = it,
                    fontSize = 16,
                    showSettings = { /*TODO*/ },
                    showBottomBar = true,
                    navController = rememberNavController()
                )
            }
        }
    }
}