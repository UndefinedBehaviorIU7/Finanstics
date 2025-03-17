package com.example.finanstics.presentation.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finanstics.presentation.forms.Form
import com.example.finanstics.presentation.forms.ButtonForm
import com.example.finanstics.presentation.forms.ImageForm
import com.example.finanstics.R


@Composable
fun Register(navController: NavController, vm: RegisterViewModel = viewModel()) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            vm.imageChange(uri)
        }
    )
    val uiState = vm.uiState.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(0.2f))

            if (uiState !is SignupUiState.Success && uiState !is SignupUiState.Loading) {
                Image(
                    painter = painterResource(R.drawable.placeholder),
                    modifier = Modifier.size(280.dp).weight(1f),
                    contentDescription = ""
                )
            }
            when (uiState) {
                is SignupUiState.Idle -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(2.5f).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(start = 60.dp, end = 60.dp)) {
                            Form(uiState.username, label = "Имя пользователя",
                                isError = false, lambda = { vm.loginChange(it) })

                            Form(uiState.tag, label = "Тэг",
                                isError = false, lambda = { vm.tagChange(it) })

                            Form(uiState.password, label = "Пароль",
                                isError = false, lambda = { vm.passwordChange(it) })

                            Form(uiState.passwordRepeat,
                                label = "Повтор пароля",
                                isError = false,
                                lambda = { vm.passwordRepeatChange(it) })
                        }

                        ImageForm(uiState.image, text = "Выберите изображение",
                            lambda = { launcher.launch("image/*") })

                        Spacer(modifier = Modifier.height(60.dp))
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = "Зарегистрироваться",
                        navText = "Уже есть аккаунт",
                        action = {vm.signup()},
                        navigate = TODO()
                    )

                    Spacer(modifier = Modifier.weight(0.5f))
                }

                is SignupUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(2.5f).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(start = 60.dp, end = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Form(uiState.username, label = "Имя пользователя",
                                isError = true, lambda = { vm.loginChange(it) })

                            Form(uiState.tag, label = "Тэг",
                                isError = true, lambda = { vm.tagChange(it) })

                            Form(uiState.password, label = "Пароль",
                                isError = true, lambda = { vm.passwordChange(it) })

                            Form(uiState.passwordRepeat,
                                label = "Повтор пароля",
                                isError = true,
                                lambda = { vm.passwordRepeatChange(it) })
                        }

                        ImageForm(uiState.image, text = "Изображение", lambda = { launcher.launch("image/*") })
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = "Зарегистрироваться",
                        navText = "Данный пользователь уже зарегистрирован",
                        action = {vm.signup()},
                        navigate = TODO()
                    )

                    Spacer(modifier = Modifier.weight(0.5f))
                }

                is SignupUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SignupUiState.Success -> {
                    TODO("navigation")
                }
            }
        }
    }
}
