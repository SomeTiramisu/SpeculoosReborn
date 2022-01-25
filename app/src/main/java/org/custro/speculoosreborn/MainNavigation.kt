package org.custro.speculoosreborn

import android.app.Activity
import android.app.Notification
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@ExperimentalMaterial3Api
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
            //SettingsScreen()
            AndroidView(factory = {
                (it as Activity).findViewById(R.id.preference_container_view)
            })
        }
    }
}
