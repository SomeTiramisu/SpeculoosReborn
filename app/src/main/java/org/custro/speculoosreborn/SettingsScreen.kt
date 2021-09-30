package org.custro.speculoosreborn

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

@Composable
fun SettingsScreen(model: ViewModel = SettingsModel()) {
    LazyColumn {
        item {
            //settingsItem(text = "background") {
            //}
        }


    }
}


@Composable
fun settingsItem(text: String, icon: ImageVector? = null, toggle: @Composable () -> Unit) {
    Row {
        if(icon != null) {
            Icon(icon, null)
        }
        Text(text = text)
        toggle()
    }
}