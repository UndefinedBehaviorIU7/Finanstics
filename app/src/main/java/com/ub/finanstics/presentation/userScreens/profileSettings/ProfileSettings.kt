package com.ub.finanstics.presentation.userScreens.profileSettings

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.templates.BasicDialog
import com.ub.finanstics.presentation.templates.LoadingContent
import com.ub.finanstics.presentation.templates.Toggler
import com.ub.finanstics.ui.theme.ThemeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("ForbiddenComment", "MagicNumber")
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
            .systemBarsPadding()
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 100.dp),
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

                    is ProfileSettingsUiState.Error -> ErrorContent(onRetry = vm::load,
                        { vm.offlineMode(isDark) },
                        isDark = isDark)

                    is ProfileSettingsUiState.Loading -> LoadingContent()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
private fun AuthContent(
    state: ProfileSettingsUiState.Auth,
    isDark: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    vm: ProfileSettingsViewModel
) {
    val context = LocalContext.current
    remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val notificationsEnabled by remember {
        mutableStateOf(
            NotificationManagerCompat.from(context)
                .areNotificationsEnabled()
        )
    }

    val initialUserData = remember { state.userData }
    var lastSavedData by remember { mutableStateOf(initialUserData) }
    val currentData = state.userData
    val isDataChanged = currentData != lastSavedData

    key(state.showPasswordDialog) {
        if (state.showPasswordDialog) {
            BasicDialog(
                onDismissRequest = { vm.onShowPasswordChange(false) },
                content = { PasswordChangeDialog(
                    onClose = { vm.onShowPasswordChange(false) },
                    onChange = { oldPwd, newPwd -> vm.changePassword(oldPwd, newPwd) },
                    isError = state.passwordChangeError
                ) }
            )
        }
    }

    key(state.showPasswordChangeToast) {
        if (state.showPasswordChangeToast) {
            Toast.makeText(
                LocalContext.current,
                stringResource(R.string.password_change_success),
                Toast.LENGTH_SHORT).show()
                vm.onShowPasswordToastChange(false)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileHeader(username = state.username, tag = state.tag, image = state.imageBitmap, vm)

        OutlinedTextField(
            value = state.userData,
            onValueChange = { vm.onDataChange(it) },
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
                        focusManager.clearFocus()
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
            checked = notificationsEnabled,
            onToggle = {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { intent ->
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                }
            },
            fontSize = 20.sp
        )

        Text(
            text = stringResource(R.string.change_password),
            fontSize = 18.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = { vm.onShowPasswordChange(true) })
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

        Spacer(modifier = Modifier.height(16.dp))
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
    }
}

@Suppress("MagicNumber")
@Composable
private fun ErrorContent(onRetry: () -> Unit,
                         onClick: (Boolean) -> Unit,
                         isDark: Boolean,
) {
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
        Text(
            text = stringResource(R.string.offline),
            fontSize = 18.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = { onClick(isDark) })
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

@Suppress("MagicNumber", "LongMethod")
@Composable
private fun ProfileHeader(
    username: String,
    tag: String,
    image: Bitmap?,
    vm: ProfileSettingsViewModel,
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var editableName by rememberSaveable { mutableStateOf(username) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    val painter = image?.asImageBitmap()?.let { BitmapPainter(it) }
        ?: painterResource(R.drawable.profile_placeholder)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            vm.imageChange(uri)
        }
    )

    Box(
        modifier = Modifier
            .size(180.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.profile_image_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable(onClick = { launcher.launch("image/*") })
        )

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
                .zIndex(1f)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier
                    .size(30.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }

    CenteredEditableText(
        currentName = editableName,
        onNameChanged = { editableName = it },
        isEditing = isEditing,
        onEditClick = {
            isEditing = true
        },
        onSaveClick = {
            isEditing = false
            vm.onUsernameChange(editableName)
            vm.saveUsername(editableName)
        },
        focusRequester = focusRequester,
    )

    Text(
        text = "Тег: $tag",
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Suppress("MagicNumber", "LongParameterList")
@Composable
fun CenteredEditableText(
    currentName: String,
    onNameChanged: (String) -> Unit,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontSize = 28.sp,
        textAlign = TextAlign.Center
    )
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        if (isEditing) {
            TextField(
                value = currentName,
                onValueChange = onNameChanged,
                singleLine = true,
                textStyle = textStyle,
                modifier = Modifier
                    .align(Alignment.Center)
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
            }
        } else {
            Text(
                text = currentName,
                style = textStyle,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun PasswordChangeDialog(
    onClose: () -> Unit,
    onChange: (oldPassword: String, newPassword: String) -> Unit,
    isError: Boolean
) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var oldPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isError) BorderStroke(width = 2.dp, color = Color.Red) else
            BorderStroke(width = 2.dp, color = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.change_password),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text(stringResource(R.string.current_password)) },
                singleLine = true,
                visualTransformation = if (oldPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { oldPasswordVisible = !oldPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (oldPasswordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (oldPasswordVisible)
                                stringResource(R.string.hide_password)
                            else
                                stringResource(R.string.show_password)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(stringResource(R.string.new_password)) },
                singleLine = true,
                visualTransformation = if (newPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { newPasswordVisible = !newPasswordVisible }
                    ) {
                        Icon(
                            imageVector = if (newPasswordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (newPasswordVisible)
                                stringResource(R.string.hide_password)
                            else
                                stringResource(R.string.show_password)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onClose) {
                    Text(stringResource(R.string.cancellation))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.primary),
                    onClick = { onChange(oldPassword, newPassword) },
                    enabled = oldPassword.isNotBlank() && newPassword.isNotBlank()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
