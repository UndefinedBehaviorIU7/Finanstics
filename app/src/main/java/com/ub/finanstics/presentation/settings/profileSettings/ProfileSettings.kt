package com.ub.finanstics.presentation.settings.profileSettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun Toggler(text: String, checked: Boolean, action: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = text, fontSize = 20.sp)
        Switch(
            checked = checked,
            onCheckedChange = action
        )
    }
}

@Composable
fun ProfileSettings(navController: NavController, vm: ProfileSettingsViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value

    when (uiState) {
        is ProfileSettingsUiState.Auth -> {
            Toggler("aboba", false, {})
        }

        is ProfileSettingsUiState.NotAuth -> {

        }

        is ProfileSettingsUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = uiState.msg, fontSize = 30.sp, color = Color.Red)
            }
        }
    }
}
