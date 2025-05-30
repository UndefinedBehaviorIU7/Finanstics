package com.ub.finanstics.presentation.userScreens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.templates.BackArrow
import com.ub.finanstics.presentation.templates.DoubleButton
import com.ub.finanstics.presentation.templates.ErrorAlertDialog
import com.ub.finanstics.presentation.templates.ErrorDialogContent
import com.ub.finanstics.presentation.templates.Form
import com.ub.finanstics.presentation.templates.Loader
import com.ub.finanstics.presentation.templates.PasswordForm
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario

@Suppress("MagicNumber", "LongMethod")
@Composable
fun Login(navController: NavController, vm: LoginViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                BackArrow { navController.navigate(Navigation.STATS.toString()) }
            }

            Spacer(modifier = Modifier.weight(0.7f))

            when (uiState) {
                is LoginUiState.Idle -> {
                    LoginIdle(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is LoginUiState.Error -> {
                    LoginError(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is LoginUiState.Loading -> {
                    BoxWithConstraints {
                        val width = maxWidth
                        Loader(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(width / 3)
                        )
                    }
                }

                is LoginUiState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Navigation.STATS.toString())
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun LoginIdle(
    navController: NavController,
    vm: LoginViewModel,
    uiState: LoginUiState.Idle
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
            contentDescription = stringResource(R.string.app_name)
        )

        Form(
            value = uiState.login,
            label = stringResource(R.string.login),
            isError = false,
            lambda = { vm.updateField("login", it) }
        )

        PasswordForm(
            value = uiState.password,
            label = stringResource(R.string.password),
            isError = false,
            lambda = { vm.updateField("password", it) }
        )

        Spacer(modifier = Modifier.weight(0.1f))

        DoubleButton(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(R.string.log_in),
            navText = stringResource(R.string.register),
            action = { vm.logIn() },
            navigate = { navController.navigate(Navigation.REGISTER.toString()) }
        )

        OneTap(
            onAuth = { oAuth, token ->
                vm.handleOneTapAuth(token)
            },
            scenario = OneTapTitleScenario.SignIn,
        )

        Spacer(modifier = Modifier.weight(0.25f))
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun LoginError(
    navController: NavController,
    vm: LoginViewModel,
    uiState: LoginUiState.Error
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
            contentDescription = stringResource(R.string.app_name)
        )

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

        ErrorAlertDialog(
            onDismissRequest = { vm.resetToIdle() }
        ) {
            ErrorDialogContent(
                msg = uiState.errorMsg,
                action = {
                    vm.resetToIdle()
                },
                buttonText = stringResource(R.string.ok),
                onClose = {
                    vm.resetToIdle()
                }
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        DoubleButton(
            modifier = Modifier
                .fillMaxWidth(),
            buttonText = stringResource(R.string.log_in),
            navText = stringResource(R.string.register),
            action = { vm.logIn() },
            navigate = { navController.navigate(Navigation.REGISTER.toString()) }
        )
        OneTap(
            onAuth = { oAuth, token ->
                vm.handleOneTapAuth(token)
            },
            scenario = OneTapTitleScenario.SignIn,
        )

        Spacer(modifier = Modifier.weight(0.25f))
    }
}