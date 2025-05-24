package com.ub.finanstics.presentation.addAction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.forms.Form

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun BooleanFormCheckboxField(
    value: Boolean,
    label: String,
    isError: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = if (value) "Включено" else "Выключено",
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

    Text(
        text = "Тип действия: ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 26.sp
    )

    Form(
        value = uiState.nameAction,
        label = "Название действия",
        isError = false,
        lambda = { vm.updateUIState(newNameAction = it) }
    )

    Form(
        value = if (uiState.moneyAction != -1) uiState.moneyAction.toString() else "",
        label = "Сколько Бабла",
        isError = false,
        lambda = { vm.updateUIState(newMoneyAction = it.toIntOrNull() ?: -1) }
    )

    FormAddData(
        value = uiState.data,
        label = "Дата",
        isError = false,
        lambda = { vm.updateUIState(newData = it) }
    )

    Selector(
        value = uiState.category,
        label = "Категория",
        expanded = uiState.menuExpandedCategory,
        allElements = uiState.allCategory,
        onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
        selected = { vm.updateUIState(newCategory = it) },
        isError = false
    )

    Form(
        value = uiState.description,
        label = "Описание",
        isError = false,
        lambda = { vm.updateUIState(newDescription = it) }
    )

    BooleanFormCheckboxField(
        value = uiState.duplication,
        label = "продублировать к себе",
        isError = false,
        onCheckedChange = { vm.updateUIState(newDuplication = it) },
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val width = maxWidth
        Button(
            onClick = {
                vm.addAction()
                navController.popBackStack()
            },
            enabled = vm.validateIdle(uiState) == Error.OK
        ) {
            Text(
                text = "Добавить",
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
    error: Error
) {
    Text(
        text = "Тип действия: ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 26.sp
    )

    Form(
        value = uiState.nameAction,
        label = "Название действия",
        isError = error == Error.NAME,
        lambda = { vm.updateUIState(newNameAction = it) }
    )

    Form(
        value = if (uiState.moneyAction != -1) uiState.moneyAction.toString() else "",
        label = "Сколько Бабла",
        isError = error == Error.MONEY,
        lambda = { vm.updateUIState(newMoneyAction = it.toIntOrNull() ?: -1) }
    )

    FormAddData(
        value = uiState.data,
        label = "Дата",
        isError = error == Error.DATE,
        lambda = { vm.updateUIState(newData = it) }
    )

    Selector(
        value = uiState.category,
        label = "Категория",
        expanded = uiState.menuExpandedCategory,
        allElements = uiState.allCategory,
        onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
        selected = { vm.updateUIState(newCategory = it) },
        isError = false
    )

    Form(
        value = uiState.description,
        label = "Описание",
        isError = error == Error.DESCRIPTION,
        lambda = { vm.updateUIState(newDescription = it) }
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val width = maxWidth
        Button(
            onClick = { vm.addAction() },
        ) {
            Text(
                text = "Добавить",
                fontSize = 28.sp
            )
        }
    }
    Text(
        text = "ошибка ${error.str}",
        fontSize = 28.sp
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun AddActionGroup(
    navController: NavController
) {
    val vm: AddActionGroupViewModel = viewModel()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 20.dp,
                start = 5.dp,
                end = 5.dp
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
                    text = "Добавление действия",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp
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

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val uiState = vm.uiState.collectAsState().value) {
            is AddActionGroupUiState.Idle -> {
                DrawIdleGroup(uiState, vm, navController)
            }

            is AddActionGroupUiState.Error -> {
                DrawErrorGroup(uiState, vm, uiState.error)
            }

            is AddActionGroupUiState.Ok -> {}

            is AddActionGroupUiState.СhoiceType -> {
                Text(
                    text = "Выберете тип действия",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 26.sp
                )
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { vm.chooseTypeAndLoad(ActionType.EXPENSE) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            stringResource(R.string.expense),
                            fontSize = 22.sp
                        )
                    }

                    Button(
                        onClick = { vm.chooseTypeAndLoad(ActionType.INCOME) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            stringResource(R.string.income),
                            fontSize = 22.sp
                        )
                    }
                }
            }
            else -> {}
        }
    }
}
