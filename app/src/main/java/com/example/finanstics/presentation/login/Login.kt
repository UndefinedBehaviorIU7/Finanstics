package com.example.finanstics.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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


@Suppress("MagicNumber", "LongMethod")
@Composable
fun Login(navController: NavController, vm: LoginViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.step_back),
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.7f))

            if (uiState !is LoginUiState.Success && uiState !is LoginUiState.Loading) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    modifier = Modifier.size(280.dp).weight(1f),
                    contentDescription = ""
                )
            }

            when (uiState) {
                is LoginUiState.Idle -> {
                    Column(
                        modifier = Modifier.weight(1.2f)
                    ) {

                        Column(modifier = Modifier.padding(start = 60.dp, end = 60.dp)) {

                            Form(
                                value = uiState.login,
                                label = stringResource(R.string.login),
                                isError = false,
                                lambda = { vm.updateField("login", it) }
                            )

                            Form(
                                value = uiState.password,
                                label = stringResource(R.string.password),
                                isError = false,
                                lambda = { vm.updateField("password", it) }
                            )
                        }

                        Spacer(modifier = Modifier.weight(0.5f))
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = stringResource(R.string.log_in),
                        navText = stringResource(R.string.register),
                        action = { vm.logIn() },
                        navigate = { navController.navigate(Navigation.REGISTER.toString()) }
                    )

                    Spacer(modifier = Modifier.weight(0.6f))
                }

                is LoginUiState.Error -> {
                    Column(
                        modifier = Modifier.weight(1.3f)
                    ) {

                        Column(modifier = Modifier.padding(start = 60.dp, end = 60.dp)) {
                            Form(
                                value = uiState.login,
                                label = stringResource(R.string.login),
                                isError = true,
                                lambda = { vm.updateField("login", it) }
                            )

                            Form(
                                value = uiState.password,
                                label = stringResource(R.string.password),
                                isError = true,
                                lambda = { vm.updateField("password", it) }
                            )

                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = uiState.errorMsg,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                    .weight(0.5f),
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                    }

                    ButtonForm(
                        modifier = Modifier.weight(0.5f),
                        buttonText = stringResource(R.string.log_in),
                        navText = stringResource(R.string.register),
                        action = { vm.logIn() },
                        navigate = { navController.navigate(Navigation.REGISTER.toString()) }
                    )

                    Spacer(modifier = Modifier.weight(0.6f))
                }

                is LoginUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoginUiState.Success -> {
                    navController.navigate(Navigation.GROUPS.toString())
                }
            }
        }
    }
}
