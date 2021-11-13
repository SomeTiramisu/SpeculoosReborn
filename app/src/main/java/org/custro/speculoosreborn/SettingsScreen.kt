package org.custro.speculoosreborn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceStringSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsMenuLink

@Composable
fun SettingsScreen(model: SettingsModel = viewModel()) {
    val bordersState = rememberPreferenceBooleanSettingState(key = "borders_pref_key", defaultValue = true)
    val scaleState = rememberPreferenceBooleanSettingState(key = "scale_pref_key", defaultValue = true)
    val maskState = rememberPreferenceBooleanSettingState(key = "mask_pref_key", defaultValue = true)
    val cropState = rememberPreferenceBooleanSettingState(key = "crop_pref_key", defaultValue = true)
    val backgroundState = rememberPreferenceStringSettingState(key = "background_pref_key", defaultValue = "file:///android_asset/background.png")
    Column() {
        SettingsCheckbox(state = bordersState,
            title = { Text("Borders")},
            subtitle = { Text("Add black borders to fit") }) {
        }
        SettingsCheckbox(state = scaleState,
            title = { Text("Scale")},
            subtitle = { Text("Scale image to for pixel-perfect fit") }) {
        }
        SettingsCheckbox(state = maskState,
            title = { Text("Mask")},
            subtitle = { Text("Makes white transparent") }) {
        }
        SettingsCheckbox(state = cropState,
            title = { Text("Auto crop")},
            subtitle = { Text("Crop image black borders") }) {
        }
        SettingsMenuLink(title = { Text("Background") }) {
            
        }
        SettingsMenuLink(title = { Text("Reset library") }) {
            
        }

    }
}
