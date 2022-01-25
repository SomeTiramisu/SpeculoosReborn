package org.custro.speculoosreborn.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.custro.speculoosreborn.ui.model.SettingsModel

@Composable
fun SettingsScreen(model: SettingsModel = viewModel()) {/*
    val bordersState = rememberPreferenceBooleanSettingState(key = "borders_pref_key", defaultValue = true)
    val scaleState = rememberPreferenceBooleanSettingState(key = "scale_pref_key", defaultValue = true)
    val maskState = rememberPreferenceBooleanSettingState(key = "mask_pref_key", defaultValue = true)
    val cropState = rememberPreferenceBooleanSettingState(key = "crop_pref_key", defaultValue = true)
    val backgroundState = rememberPreferenceStringSettingState(key = "background_pref_key", defaultValue = "file:///android_asset/background.png")
    Surface {
        Column {
            SettingsCheckbox(state = bordersState,
                title = { Text("Borders") },
                subtitle = { Text("Add black borders to fit screen") }) {
            }
            SettingsCheckbox(state = scaleState,
                title = { Text("Scale") },
                subtitle = { Text("Scale image to for pixel-perfect fit") }) {
            }
            SettingsCheckbox(state = maskState,
                title = { Text("Mask") },
                subtitle = { Text("Makes white transparent") }) {
            }
            SettingsCheckbox(state = cropState,
                title = { Text("Auto crop") },
                subtitle = { Text("Crop image black borders") }) {
            }
            SettingsMenuLink(title = { Text("Background") }) {

            }
            SettingsMenuLink(title = { Text("Reset library") }) {

            }

        }
    }*/
}
