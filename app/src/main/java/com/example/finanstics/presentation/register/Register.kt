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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.R
import com.example.finanstics.presentation.Navigation
import com.example.finanstics.presentation.forms.ButtonForm
import com.example.finanstics.presentation.forms.Form
import com.example.finanstics.presentation.forms.ImageForm

@Suppress("MagicNumber", "LongMethod")
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

            if ((uiState is RegisterUiState.Idle) || (uiState is RegisterUiState.Error)) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    modifier = Modifier.size(280.dp).weight(1f),
                    contentDescription = ""
                )
            }

            when (uiState) {
                is RegisterUiState.Idle -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(2.5f).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(start = 60.dp, end = 60.dp)) {
                            Form(
                                uiState.login,
                                label = stringResource(R.string.login),
                                isError = false,
                                lambda = { vm.loginChange(it) }
                            )

                            Form(
                                uiState.tag,
                                label = stringResource(R.string.username),
                                isError = false,
                                lambda = { vm.tagChange(it) }
                            )

                            Form(
                                uiState.password,
                                label = stringResource(R.string.password),
                                isError = false,
                                lambda = { vm.passwordChange(it) }
                            )

                            Form(
                                uiState.passwordRepeat,
                                label = stringResource(R.string.password_repeat),
                                isError = false,
                                lambda = { vm.passwordRepeatChange(it) }
                            )
                        }

                        ImageForm(
                            uiState.image,
                            text = stringResource(R.string.select_img),
                            lambda = { launcher.launch("image/*") }
                        )

                        Spacer(modifier = Modifier.height(60.dp))
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = stringResource(R.string.register),
                        navText = stringResource(R.string.have_an_account),
                        action = { vm.signup() },
                        navigate = { navController.navigate(Navigation.LOGIN.toString()) }
                    )

                    Spacer(modifier = Modifier.weight(0.5f))
                }

                is RegisterUiState.Error -> {
                    Text(
                        text = uiState.errorMsg,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(2.5f).fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 60.dp, end = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Form(
                                uiState.login,
                                label = stringResource(R.string.login),
                                isError = true,
                                lambda = { vm.loginChange(it) }
                            )

                            Form(
                                uiState.tag,
                                label = stringResource(R.string.username),
                                isError = true,
                                lambda = { vm.tagChange(it) }
                            )

                            Form(
                                uiState.password,
                                label = stringResource(R.string.password),
                                isError = true,
                                lambda = { vm.passwordChange(it) }
                            )

                            Form(
                                uiState.passwordRepeat,
                                label = stringResource(R.string.password_repeat),
                                isError = true,
                                lambda = { vm.passwordRepeatChange(it) }
                            )
                        }

                        ImageForm(
                            uiState.image,
                            text = stringResource(R.string.select_img),
                            lambda = { launcher.launch("image/*") }
                        )
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = stringResource(R.string.register),
                        navText = stringResource(R.string.have_an_account),
                        action = { vm.signup() },
                        navigate = { navController.navigate(Navigation.LOGIN.toString()) }
                    )

                    Spacer(modifier = Modifier.weight(0.5f))
                }

                is RegisterUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is RegisterUiState.Success -> {
                    navController.navigate(Navigation.LOGIN.toString())
                }
            }
        }
    }
}
