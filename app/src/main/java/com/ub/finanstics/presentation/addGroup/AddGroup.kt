package com.ub.finanstics.presentation.addGroup

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.dialogs.ErrorAlertDialog
import com.ub.finanstics.dialogs.ErrorDialogContent
import com.ub.finanstics.presentation.forms.Form
import com.ub.finanstics.presentation.templates.ErrorContent
import com.ub.finanstics.presentation.templates.LoadingContent

@Suppress("MagicNumber", "ComplexMethod", "LongMethod")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AddGroupScreen(
    navController: NavController,
    vm: AddGroupViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    val windowSize = calculateWindowSizeClass(activity = LocalContext.current as ComponentActivity)

    val horizontalPadding = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 32.dp
        WindowWidthSizeClass.Medium -> 64.dp
        WindowWidthSizeClass.Expanded -> 120.dp
        else -> 32.dp
    }

    val verticalPadding = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 32.dp
        WindowWidthSizeClass.Medium -> 48.dp
        WindowWidthSizeClass.Expanded -> 64.dp
        else -> 32.dp
    }

    val verticalGap = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 32.dp
        WindowWidthSizeClass.Expanded -> 24.dp
        else -> 16.dp
    }

    val headerPadding = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 12.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 32.dp
        else -> 12.dp
    }

    val contentMargin = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 24.dp
        WindowWidthSizeClass.Expanded -> 32.dp
        else -> 16.dp
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = verticalPadding)
            .systemBarsPadding()
    ) {
        val (headerRef, contentRef) = createRefs()

        Header(
            navController = navController,
            windowSizeClass = windowSize.widthSizeClass,
            headerPadding = headerPadding,
            modifier = Modifier.constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Box(
            modifier = Modifier
                .constrainAs(contentRef) {
                    top.linkTo(headerRef.bottom, margin = contentMargin)
                    bottom.linkTo(parent.bottom, margin = verticalPadding)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
                .padding(horizontal = horizontalPadding)
        ) {
            when (uiState) {
                is AddGroupUiState.Idle -> {
                    IdleContent(
                        vm = vm,
                        verticalGap = verticalGap,
                        windowSizeClass = windowSize.widthSizeClass,
                        uiState = uiState as AddGroupUiState.Idle
                    )
                }

                is AddGroupUiState.Loading -> {
                    LoadingContent()
                }

                is AddGroupUiState.Error -> {
                    ErrorContent(onRetry = { vm.createGroup() })
                }

                is AddGroupUiState.Success -> {
                    LaunchedEffect(uiState) {
                        navController.navigateUp()
                        vm.clearSuccessFlag()
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber", "ComplexMethod", "LongMethod")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun IdleContent(
    vm: AddGroupViewModel,
    verticalGap: Dp,
    windowSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    uiState: AddGroupUiState.Idle
) {
    val buttonHeight = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 56.dp
        WindowWidthSizeClass.Medium -> 64.dp
        WindowWidthSizeClass.Expanded -> 72.dp
        else -> 56.dp
    }

    val buttonWidthFraction = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 0.8f
        WindowWidthSizeClass.Medium -> 0.6f
        WindowWidthSizeClass.Expanded -> 0.4f
        else -> 0.8f
    }

    val fontSize = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 18.sp
        WindowWidthSizeClass.Medium -> 28.sp
        WindowWidthSizeClass.Expanded -> 22.sp
        else -> 18.sp
    }

    key(uiState.showDialog) {
        if (uiState.showDialog) {
            if (uiState.nameError || uiState.tagError) {
                ErrorAlertDialog(
                    onDismissRequest = { vm.dismissErrorDialog() },
                    content = { ErrorDialogContent(
                        uiState.errorMsg,
                        buttonText = stringResource(R.string.ok),
                        action = { vm.dismissErrorDialog() },
                        onClose = { vm.dismissErrorDialog() }
                    ) }
                )
            } else {
                ErrorAlertDialog(
                    onDismissRequest = { vm.dismissErrorDialog() },
                    content = { ErrorDialogContent(
                        uiState.errorMsg,
                        buttonText = stringResource(R.string.retry),
                        action = { vm.createGroup() },
                        onClose = { vm.dismissErrorDialog() }
                    ) }
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(verticalGap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Form(
            value = uiState.groupName,
            label = stringResource(R.string.group_name),
            isError = uiState.nameError,
            lambda = { vm.updateUiState(newName = it, newNameErr = false) }
        )
        Form(
            value = uiState.groupData,
            label = stringResource(R.string.group_data),
            isError = uiState.dataError,
            lambda = { vm.updateUiState(newData = it, newDataErr = false) }
        )
        Form(
            value = uiState.userInput,
            label = stringResource(R.string.users),
            isError = uiState.tagError,
            lambda = { vm.updateUiState(newUserInput = it) },
            icon = {
                if (uiState.userInput.isNotEmpty()) {
                    IconButton(onClick = {
                        vm.addTag(uiState.userInput)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                }
            }
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.users.forEach { user ->
                Chip(onClick = { vm.deleteTag(user.tag) }) {
                    Text(user.tag)
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { vm.createGroup() },
            modifier = Modifier
                .fillMaxWidth(buttonWidthFraction)
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.add_group),
                fontSize = fontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun Header(
    navController: NavController,
    windowSizeClass: WindowWidthSizeClass,
    headerPadding: Dp,
    modifier: Modifier = Modifier
) {
    val iconSize = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 28.dp
        WindowWidthSizeClass.Medium -> 32.dp
        WindowWidthSizeClass.Expanded -> 36.dp
        else -> 28.dp
    }

    val titleFontSize = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 24.sp
        WindowWidthSizeClass.Medium -> 28.sp
        WindowWidthSizeClass.Expanded -> 32.sp
        else -> 24.sp
    }

    val titleMargin = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> 12.dp
        WindowWidthSizeClass.Medium -> 16.dp
        WindowWidthSizeClass.Expanded -> 20.dp
        else -> 12.dp
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = headerPadding)
    ) {
        val (backBtn, titleText) = createRefs()

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.constrainAs(backBtn) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.step_back),
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = stringResource(R.string.add_group_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = titleFontSize,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(backBtn.end, margin = titleMargin)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
    }
}
