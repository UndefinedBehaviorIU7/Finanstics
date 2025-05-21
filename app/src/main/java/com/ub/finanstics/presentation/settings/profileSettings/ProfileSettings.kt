package com.ub.finanstics.presentation.settings.profileSettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ub.finanstics.R

@Composable
fun Toggler(text: String, checked: Boolean, action: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(3f), text = text, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(2f))
        Switch(
            modifier = Modifier.weight(1f),
            checked = checked,
            onCheckedChange = action
        )
    }
}

@Composable
fun ProfileSettings(navController: NavController, vm: ProfileSettingsViewModel = viewModel()) {
    when (val uiState = vm.uiState.collectAsState().value) {
        is ProfileSettingsUiState.Auth -> {
            Column {
                Spacer(modifier = Modifier.weight(1.7f))
                Row(modifier = Modifier.fillMaxSize().weight(6f)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(modifier = Modifier.weight(4f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(modifier = Modifier.weight(5f), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (uiState.image == null) {
                                Image(
                                    painter = painterResource(R.drawable.profile_placeholder),
                                    "placeholder_avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.weight(2f).clip(CircleShape).aspectRatio(1f)
                                )
                            } else {
                                Image(
                                    painter = BitmapPainter(uiState.image.asImageBitmap()),
                                    "placeholder_avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.weight(2f).clip(CircleShape).aspectRatio(1f)
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.3f))
                            Text(
                                text = uiState.username,
                                fontSize = 30.sp,
                                modifier = Modifier.weight(0.7f)
                            )
                            // TODO: поля ввода "о себе" добавить изменение статуса
                            if (uiState.userData != null) {
                                OutlinedTextField(
                                    onValueChange = {},
                                    value = uiState.userData,
                                    label = { Text(stringResource(R.string.about).toString()) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                OutlinedTextField(
                                    onValueChange = {},
                                    value = stringResource(R.string.empty_user_data),
                                    label = { Text(stringResource(R.string.about).toString()) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(3f)) {
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.weight(1f)) {
                                Toggler(text = stringResource(R.string.night_mode), false, {})
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                Toggler(text = stringResource(R.string.notifications), false, {})
                            }
                            Spacer(modifier = Modifier.weight(1.5f))
                        }

                        // TODO: переделать на логаут
                        Button(onClick = {},
                            modifier = Modifier.weight(0.7f).width(180.dp)) {
                            Text(stringResource(R.string.log_out), fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.weight(2f))
            }
        }

        is ProfileSettingsUiState.NotAuth -> {

        }

        // TODO: добавить кнопку "Повторить запрос"
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
