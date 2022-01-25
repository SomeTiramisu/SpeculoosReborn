package org.custro.speculoosreborn

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.custro.speculoosreborn.ui.FilePickerScreen
import org.custro.speculoosreborn.ui.InitScreen
import org.custro.speculoosreborn.ui.ReaderScreen
import org.custro.speculoosreborn.ui.SettingsScreen
import org.custro.speculoosreborn.ui.model.InitModel
import org.custro.speculoosreborn.ui.model.ReaderModel

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
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
