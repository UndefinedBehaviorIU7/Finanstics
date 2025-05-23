package com.ub.finanstics.presentation.settings.profileSettings

import android.app.UiModeManager
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.sqlite.db.SupportSQLiteOpenHelper
import coil3.DrawableImage
import com.ub.finanstics.R
import com.ub.finanstics.ui.theme.ThemeViewModel
import com.ub.finanstics.presentation.Navigation

/* TODO: выбор картинки, logout, редирект с регистрации, обновление юзер даты,
    пофиксить обновление экрана после логина, пермишены на уведомления,
    НЕ ЗАБЫТЬ ПОМЕНЯТЬ ТЕСТОВЫЙ СЕРВЕР
 */

@Composable
fun Toggler(text: String, checked: Boolean, action: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(3f), text = text, fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.weight(2f))
        Switch(
            modifier = Modifier.weight(1f),
            checked = checked,
            onCheckedChange = action
        )
    }
}

@Composable
fun ProfileSettings(navController: NavController, vm: ProfileSettingsViewModel = viewModel(),
                    themeVm: ThemeViewModel) {
    val uiState = vm.uiState.collectAsState().value
    val isDark by themeVm.isDark.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.onScreenEnter()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box (modifier = Modifier.background(MaterialTheme.colorScheme.background).statusBarsPadding().fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.weight(1.7f))
            Row(modifier = Modifier.fillMaxSize().weight(6f)) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (uiState) {
                        is ProfileSettingsUiState.Auth -> {
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
                                    modifier = Modifier.weight(0.7f),
                                    color = MaterialTheme.colorScheme.primary
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
                                    Toggler(text = stringResource(R.string.night_mode),
                                        checked = isDark,
                                        action = { themeVm.toggleDarkMode(it) })
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    Toggler(text = stringResource(R.string.notifications), false, {})
                                }
                                Spacer(modifier = Modifier.weight(1.5f))
                            }

                            Button(onClick = {
                                vm.logout()

                                navController.popBackStack(
                                    route = Navigation.STATS.toString(),
                                    inclusive = true
                                )

                                navController.navigate(Navigation.STATS.toString()) {
                                    launchSingleTop = true
                                } },
                                modifier = Modifier.weight(0.7f).width(180.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground,
                                    contentColor = MaterialTheme.colorScheme.primary)) {
                                Text(stringResource(R.string.log_out), fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        is ProfileSettingsUiState.NotAuth -> {
                            Column(modifier = Modifier.weight(3f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = stringResource(R.string.not_aut), modifier = Modifier.weight(1f),
                                    fontSize = 30.sp, textAlign = TextAlign.Center,
                                    lineHeight = 30.sp, color = MaterialTheme.colorScheme.primary)
                                Button(onClick = {navController.navigate(Navigation.LOGIN.toString())},
                                    modifier = Modifier.weight(0.7f).width(180.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground,
                                        contentColor = MaterialTheme.colorScheme.primary)) {
                                    Text(stringResource(R.string.log_in), fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.weight(0.1f))
                                Text(text = stringResource(R.string.register),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { navController.navigate(Navigation.REGISTER.toString()) },
                                    textDecoration = TextDecoration.Underline,
                                    color = MaterialTheme.colorScheme.primary)
                            }

                            Column(modifier = Modifier.weight(3f)) {
                                Spacer(modifier = Modifier.weight(1f))
                                Box(modifier = Modifier.weight(1f)) {
                                    Toggler(text = stringResource(R.string.night_mode),
                                        checked = isDark,
                                        action = { themeVm.toggleDarkMode(it) }
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    Toggler(text = stringResource(R.string.notifications), false, {})
                                }
                                Spacer(modifier = Modifier.weight(1.5f))
                            }
                        }

                        is ProfileSettingsUiState.Error -> {
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                Spacer(modifier = Modifier.weight(1f))
                                Image(painter = painterResource(R.drawable.connection_error),
                                    contentDescription = stringResource(R.string.connection_error),
                                    modifier = Modifier.weight(1.5f)
                                )
                                Spacer(modifier = Modifier.weight(0.3f))
                                Text(text = stringResource(R.string.no_internet), fontSize = 24.sp,
                                    textAlign = TextAlign.Center, lineHeight = 30.sp,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.weight(0.2f))
                                Button(onClick = {vm.load()},
                                    modifier = Modifier.weight(0.7f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground,
                                        contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(text = stringResource(R.string.retry), fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.weight(2f))
                            }
                        }

                        is ProfileSettingsUiState.Loading -> {
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(82.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}
