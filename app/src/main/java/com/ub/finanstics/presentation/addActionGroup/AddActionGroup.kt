package com.ub.finanstics.presentation.addActionGroup

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.addAction.ActionType
import com.ub.finanstics.presentation.addAction.AddActionGroupUiState
import com.ub.finanstics.presentation.addAction.AddActionGroupViewModel
import com.ub.finanstics.presentation.addAction.ErrorAddAction
import com.ub.finanstics.presentation.addAction.FormAddData
import com.ub.finanstics.presentation.addAction.Selector
import com.ub.finanstics.presentation.forms.Form
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import com.ub.finanstics.ui.theme.Loader

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun BooleanFormCheckboxField(
    value: Boolean,
    label: String,
    isError: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = if (value) stringResource(R.string.on) else stringResource(R.string.off),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Checkbox(
                checked = value,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = if (isError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.secondary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(15.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawIdleGroup(
    uiState: AddActionGroupUiState.Idle,
    vm: AddActionGroupViewModel,
    navController: NavController
) {

    HorizontalDivider(
        thickness = 1.dp,
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1]
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${stringResource(R.string.type_action)} ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Selector(
        value = uiState.category,
        label = stringResource(R.string.category),
        expanded = uiState.menuExpandedCategory,
        allElements = uiState.allCategory.map { it.name },
        onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
        selected = { vm.updateUIState(newCategory = it) },
        isError = false
    )

    Form(
        value = if (uiState.moneyAction != -1) uiState.moneyAction.toString() else "",
        label = stringResource(R.string.sum),
        isError = false,
        lambda = { vm.updateUIState(newMoneyAction = it.toIntOrNull() ?: -1) }
    )

    Form(
        value = uiState.nameAction,
        label = stringResource(R.string.name_action),
        isError = false,
        lambda = { vm.updateUIState(newNameAction = it) }
    )

    FormAddData(
        value = uiState.data,
        label = stringResource(R.string.data),
        isError = false,
        lambda = { vm.updateUIState(newData = it) }
    )

    Form(
        value = uiState.description,
        label = stringResource(R.string.description),
        isError = false,
        lambda = { vm.updateUIState(newDescription = it) }
    )

    BooleanFormCheckboxField(
        value = uiState.duplication,
        label = stringResource(R.string.duplication_my),
        isError = false,
        onCheckedChange = { vm.updateUIState(newDuplication = it) },
    )

    Spacer(modifier = Modifier.height(5.dp))

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        maxWidth
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {
                vm.addAction()
            },
            enabled = vm.validateIdle(uiState) == ErrorAddAction.OK
        ) {
            Text(
                text = stringResource(R.string.add_action),
                fontSize = 28.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawErrorGroup(
    uiState: AddActionGroupUiState.Error,
    vm: AddActionGroupViewModel,
    error: ErrorAddAction
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1]
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${stringResource(R.string.type_action)} ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${error.str}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 19.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (error == ErrorAddAction.ERROR_LOADING_DATA_SERVER ||
        error == ErrorAddAction.ERROR_ADD_DATA_SERVER) {
        Image(
            painter = painterResource(R.drawable.connection_error),
            contentDescription = stringResource(R.string.connection_error),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {
                if (error == ErrorAddAction.ERROR_LOADING_DATA_SERVER)
                    vm.tryLoad(uiState.typeAction)
                else
                    vm.addAction()
            },
        ) {
            Text(
                text = stringResource(R.string.retry),
                fontSize = 20.sp
            )
        }
    }

//    Selector(
//        value = uiState.category,
//        label = stringResource(R.string.category),
//        expanded = uiState.menuExpandedCategory,
//        allElements = uiState.allCategory.map { it.name },
//        onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
//        selected = { vm.updateUIState(newCategory = it) },
//        isError = false
//    )
//
//    Form(
//        value = if (uiState.moneyAction != -1) uiState.moneyAction.toString() else "",
//        label = stringResource(R.string.sum),
//        isError = errorAddACtion == ErrorAddAction.MONEY,
//        lambda = { vm.updateUIState(newMoneyAction = it.toIntOrNull() ?: -1) }
//    )
//
//    Form(
//        value = uiState.nameAction,
//        label = stringResource(R.string.name_action),
//        isError = errorAddACtion == ErrorAddAction.NAME,
//        lambda = { vm.updateUIState(newNameAction = it) }
//    )
//
//    FormAddData(
//        value = uiState.data,
//        label = stringResource(R.string.data),
//        isError = errorAddACtion == ErrorAddAction.DATE,
//        lambda = { vm.updateUIState(newData = it) }
//    )
//
//    Form(
//        value = uiState.description,
//        label = stringResource(R.string.description),
//        isError = errorAddACtion == ErrorAddAction.DESCRIPTION,
//        lambda = { vm.updateUIState(newDescription = it) }
//    )
//
//    BooleanFormCheckboxField(
//        value = uiState.duplication,
//        label = stringResource(R.string.duplication_my),
//        isError = false,
//        onCheckedChange = { vm.updateUIState(newDuplication = it) },
//    )
//
//    Spacer(modifier = Modifier.height(5.dp))
//
//    BoxWithConstraints(
//        modifier = Modifier.fillMaxWidth(),
//        contentAlignment = Alignment.TopCenter
//    ) {
//        maxWidth
//        Button(
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.onBackground,
//                contentColor = MaterialTheme.colorScheme.primary
//            ),
//            onClick = { vm.addAction() },
//        ) {
//            Text(
//                text = stringResource(R.string.add_action),
//                fontSize = 28.sp
//            )
//        }
//    }
}

@Composable
fun DrawSelectTypeGroup(
    vm: AddActionGroupViewModel,
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.select_type_action),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )
    Row(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { vm.tryLoad(ActionType.EXPENSE) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .border(
                    width = 2.dp,
                    color = ColorsExpenses[0],
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                stringResource(R.string.expense),
                fontSize = 22.sp
            )
        }

        Button(
            onClick = { vm.tryLoad(ActionType.INCOME) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .border(
                    width = 2.dp,
                    color = ColorsIncomes[1],
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                stringResource(R.string.income),
                fontSize = 22.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun AddActionGroup(
    navController: NavController
) {
    val windowSize = calculateWindowSizeClass(activity = LocalContext.current as ComponentActivity)

    var hasNavigatedBack by remember { mutableStateOf(false) }

    val charWidth = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.sp
        WindowWidthSizeClass.Medium -> 24.sp
        WindowWidthSizeClass.Expanded -> 26.sp
        else -> 26.sp
    }

    val vm: AddActionGroupViewModel = viewModel()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(
                top = 20.dp,
                start = 15.dp,
                end = 15.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.add_action_text),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = charWidth
                )

                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.step_back),
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        when (val uiState = vm.uiState.collectAsState().value) {
            is AddActionGroupUiState.Idle -> {
                DrawIdleGroup(uiState, vm, navController)
            }

            is AddActionGroupUiState.Error -> {
                DrawErrorGroup(uiState, vm, uiState.error)
            }

            is AddActionGroupUiState.Ok -> {
                if (!hasNavigatedBack) {
                    hasNavigatedBack = true
                    navController.popBackStack()
                }
            }

            is AddActionGroupUiState.SelectType -> {
                DrawSelectTypeGroup(vm)
            }

            is AddActionGroupUiState.Loading -> {
                BoxWithConstraints {
                    val width = maxWidth
                    Loader(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(width / 3)
                    )
                }
            }

            else -> {}
        }
    }
}
