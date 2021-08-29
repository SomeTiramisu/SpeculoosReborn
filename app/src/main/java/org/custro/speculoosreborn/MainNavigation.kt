package org.custro.speculoosreborn

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalAnimationApi
@Composable
fun MainNavigation(
    pageModel: PageModel,
    findManga: () -> Unit,
    showSystemUi: () -> Unit,
    hideSystemUi: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initScreen") {
        composable("initScreen") {
            showSystemUi()
            InitScreen(findManga = findManga, navigateToReaderScreen = {
                navController.navigate("readerScreen")
            })
        }
        composable("readerScreen") {
            hideSystemUi()
            ReaderScreen(pageModel = pageModel)
        }
    }
}
