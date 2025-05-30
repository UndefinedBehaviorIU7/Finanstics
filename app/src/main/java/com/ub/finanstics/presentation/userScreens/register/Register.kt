package com.ub.finanstics.presentation.userScreens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.templates.BackArrow
import com.ub.finanstics.presentation.templates.DoubleButton
import com.ub.finanstics.presentation.templates.ErrorAlertDialog
import com.ub.finanstics.presentation.templates.ErrorDialogContent
import com.ub.finanstics.presentation.templates.Form
import com.ub.finanstics.presentation.templates.LoadingContent
import com.ub.finanstics.presentation.templates.PasswordForm
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario

@Suppress("MagicNumber", "LongMethod", "ComplexCondition")
@Composable
fun Register(navController: NavController, vm: RegisterViewModel = viewModel()) {
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                BackArrow { navController.navigateUp() }
            }

            when (uiState) {
                is RegisterUiState.Idle -> {
                    RegisterIdle(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is RegisterUiState.VKIdle -> {
                    RegisterVkIdle(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is RegisterUiState.Error -> {
                    RegisterError(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is RegisterUiState.VKError -> {
                    RegisterVkError(
                        navController = navController,
                        vm = vm,
                        uiState = uiState
                    )
                }

                is RegisterUiState.Loading -> {
                    LoadingContent()
                }

                is RegisterUiState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Navigation.LOGIN.toString())
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun RegisterIdle(
    navController: NavController,
    vm: RegisterViewModel,
    uiState: RegisterUiState.Idle
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = stringResource(R.string.app_name)
        )

        Form(
            uiState.login,
            label = stringResource(R.string.login),
            isError = false,
            lambda = { vm.updateField("login", it) }
        )

        Form(
            uiState.username,
            label = stringResource(R.string.username),
            isError = false,
            lambda = { vm.updateField("username", it) }
        )

        PasswordForm(
            uiState.password,
            label = stringResource(R.string.password),
            isError = false,
            lambda = { vm.updateField("password", it) }
        )

        PasswordForm(
            uiState.passwordRepeat,
            label = stringResource(R.string.password_repeat),
            isError = false,
            lambda = { vm.updateField("passwordRepeat", it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        DoubleButton(
            modifier = Modifier,
            buttonText = stringResource(R.string.register),
            navText = stringResource(R.string.have_an_account),
            action = { vm.register() },
            navigate = { navController.navigate(Navigation.LOGIN.toString()) }
        )

        Spacer(modifier = Modifier.height(3.dp))

        OneTap(
            onAuth = { oAuth, token ->
                val vk = token
                vm.handleOneTapAuth(vk)
            },
            scenario = OneTapTitleScenario.SignUp,
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun RegisterVkIdle(
    navController: NavController,
    vm: RegisterViewModel,
    uiState: RegisterUiState.VKIdle
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = stringResource(R.string.app_name)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.you_need_tag),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Form(
            uiState.login,
            label = stringResource(R.string.tag),
            isError = false,
            lambda = { vm.updateField("login", it) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        DoubleButton(
            modifier = Modifier,
            buttonText = stringResource(R.string.register),
            navText = stringResource(R.string.have_an_account),
            action = { vm.registerVK() },
            navigate = { navController.navigate(Navigation.LOGIN.toString()) }
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun RegisterError(
    navController: NavController,
    vm: RegisterViewModel,
    uiState: RegisterUiState.Error
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = stringResource(R.string.app_name)
        )

        Form(
            uiState.login,
            label = stringResource(R.string.login),
            isError = false,
            lambda = { vm.updateField("login", it) }
        )

        Form(
            uiState.username,
            label = stringResource(R.string.username),
            isError = false,
            lambda = { vm.updateField("username", it) }
        )

        PasswordForm(
            uiState.password,
            label = stringResource(R.string.password),
            isError = false,
            lambda = { vm.updateField("password", it) }
        )

        PasswordForm(
            uiState.passwordRepeat,
            label = stringResource(R.string.password_repeat),
            isError = false,
            lambda = { vm.updateField("passwordRepeat", it) }
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

        Spacer(modifier = Modifier.height(10.dp))

        DoubleButton(
            modifier = Modifier,
            buttonText = stringResource(R.string.register),
            navText = stringResource(R.string.have_an_account),
            action = { vm.register() },
            navigate = { navController.navigate(Navigation.LOGIN.toString()) }
        )

        Spacer(modifier = Modifier.height(3.dp))

        OneTap(
            onAuth = { oAuth, token ->
                val vk = token
                vm.handleOneTapAuth(vk)
            },
            scenario = OneTapTitleScenario.SignUp,
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun RegisterVkError(
    navController: NavController,
    vm: RegisterViewModel,
    uiState: RegisterUiState.VKError
) {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier
                .fillMaxWidth(),
            contentDescription = stringResource(R.string.app_name)
        )

        Text(
            text = stringResource(R.string.you_need_tag),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Form(
            uiState.login,
            label = stringResource(R.string.tag),
            isError = true,
            lambda = { vm.updateField("login", it) }
        )

        ErrorAlertDialog(
            onDismissRequest = { vm.resetToVkIdle() }
        ) {
            ErrorDialogContent(
                msg = uiState.errorMsg,
                action = {
                    vm.resetToVkIdle()
                },
                buttonText = stringResource(R.string.ok),
                onClose = {
                    vm.resetToVkIdle()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        DoubleButton(
            modifier = Modifier,
            buttonText = stringResource(R.string.register),
            navText = stringResource(R.string.have_an_account),
            action = { vm.registerVK() },
            navigate = { navController.navigate(Navigation.LOGIN.toString()) }
        )

        Spacer(modifier = Modifier.height(25.dp))
    }
}
