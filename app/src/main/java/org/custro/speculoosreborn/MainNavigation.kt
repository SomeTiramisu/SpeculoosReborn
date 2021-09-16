package org.custro.speculoosreborn

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalAnimationApi
@Composable
fun MainNavigation(
    pageModel: PageModel,
    initModel: InitModel,
    findManga: () -> Unit,
    showSystemUi: () -> Unit,
    hideSystemUi: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initScreen") {
        composable("initScreen") {
            showSystemUi()
            InitScreen(initModel = initModel, findManga = findManga, setManga = { pageModel.onUriChange(it) }, navigateToReaderScreen = {
                navController.navigate("readerScreen")
            })
        }
        composable("readerScreen") {
            hideSystemUi()
            ReaderScreen(pageModel = pageModel)
        }
    }
}
