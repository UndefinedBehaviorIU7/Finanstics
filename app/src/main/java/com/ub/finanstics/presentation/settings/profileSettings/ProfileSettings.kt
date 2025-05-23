package com.ub.finanstics.presentation.settings.profileSettings

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.ui.theme.ThemeViewModel

@Suppress("ForbiddenComment", "MagicNumber")
// TODO: уведы, котлиновское апи, обновление картинки

@Composable
fun ProfileSettingsScreen(
    navController: NavController,
    vm: ProfileSettingsViewModel = viewModel(),
    themeVm: ThemeViewModel
) {
    val uiState by vm.uiState.collectAsState()
    val isDark by themeVm.isDark.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.onScreenEnter()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 100.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (uiState) {
                    is ProfileSettingsUiState.Auth -> AuthContent(
                        state = uiState as ProfileSettingsUiState.Auth,
                        isDark = isDark,
                        onDarkModeToggle = themeVm::toggleDarkMode,
                        onLogout = {
                            vm.logout()
                            navController.popBackStack(
                                Navigation.STATS.toString(),
                                inclusive = true
                            )
                            navController.navigate(Navigation.STATS.toString()) {
                                launchSingleTop = true
                            }
                        },
                        vm = vm
                    )

                    is ProfileSettingsUiState.NotAuth -> NotAuthContent(
                        onLogin = { navController.navigate(Navigation.LOGIN.toString()) },
                        onRegister = { navController.navigate(Navigation.REGISTER.toString()) },
                        isDark = isDark,
                        onDarkModeToggle = themeVm::toggleDarkMode
                    )

                    is ProfileSettingsUiState.Error -> ErrorContent(onRetry = vm::load)

                    is ProfileSettingsUiState.Loading -> LoadingContent()
                }
            }
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
private fun AuthContent(
    state: ProfileSettingsUiState.Auth,
    isDark: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    vm: ProfileSettingsViewModel
) {
    val initialUserData = remember { state.userData ?: "" }
    var lastSavedData by remember { mutableStateOf(initialUserData) }
    val currentData = state.userData ?: ""
    val isDataChanged = currentData != lastSavedData

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileHeader(username = state.username, image = state.image)

        OutlinedTextField(
            value = state.userData ?: stringResource(R.string.empty_user_data),
            onValueChange = { vm.onDataChange(it)
                            },
            label = { Text(stringResource(R.string.about)) },
            singleLine = false,
            maxLines = 3,
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            trailingIcon = {
                if (isDataChanged) {
                    IconButton(onClick = {
                        vm.saveUserData(currentData)
                        lastSavedData = currentData
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                }
            }
        )

        Toggler(
            label = stringResource(R.string.night_mode),
            checked = isDark,
            onToggle = onDarkModeToggle,
            fontSize = 20.sp
        )

        Toggler(
            label = stringResource(R.string.notifications),
            checked = false,
            onToggle = {},
            fontSize = 20.sp
        )

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                stringResource(R.string.log_out),
                fontSize = 22.sp
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun NotAuthContent(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    isDark: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.not_aut),
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.log_in), fontSize = 22.sp)
        }

        Text(
            text = stringResource(R.string.register),
            fontSize = 18.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onRegister)
        )

        Toggler(
            label = stringResource(R.string.night_mode),
            checked = isDark,
            onToggle = onDarkModeToggle,
            fontSize = 20.sp
        )

        Toggler(
            label = stringResource(R.string.notifications),
            checked = false,
            onToggle = {},
            fontSize = 20.sp
        )
    }
}

@Suppress("MagicNumber")
@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.connection_error),
            contentDescription = stringResource(R.string.connection_error),
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = stringResource(R.string.no_internet),
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.retry), fontSize = 22.sp, textAlign = TextAlign.Center)
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Suppress("MagicNumber")
@Composable
private fun ProfileHeader(username: String, image: Bitmap?) {
    val painter = image?.asImageBitmap()?.let { BitmapPainter(it) }
        ?: painterResource(R.drawable.profile_placeholder)
    Image(
        painter = painter,
        contentDescription = stringResource(R.string.profile_image_desc),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
    )
    Text(
        text = username,
        fontSize = 28.sp,
        textAlign = TextAlign.Center
    )
}

@Suppress("MagicNumber")
@Composable
fun Toggler(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    fontSize: TextUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                checkedThumbColor = MaterialTheme.colorScheme.background,
                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                uncheckedTrackColor = MaterialTheme.colorScheme.background
            )
        )
    }
}
