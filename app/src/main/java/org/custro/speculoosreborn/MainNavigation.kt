package org.custro.speculoosreborn

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun MainNavigation(
    readerModel: ReaderModel,
    initModel: InitModel,
    showSystemUi: () -> Unit,
    hideSystemUi: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initScreen") {
        composable("initScreen") {
            showSystemUi()
            InitScreen(initModel = initModel,
                findManga = {
                          navController.navigate("filePickerScreen")
            },
                setManga = { readerModel.onUriChange(it) },
                navigateToReaderScreen = {
                navController.navigate("readerScreen")
            },
            navigateToSettingsScreen = {
                navController.navigate("settingsScreen")
            })
        }
        composable("readerScreen") {
            hideSystemUi()
            ReaderScreen(readerModel = readerModel)
        }
        composable("filePickerScreen") {
            FilePickerScreen()
        }
        composable("settingsScreen") {
            SettingsScreen()
        }
    }
}
