package com.example.finanstics.presentation.addAction

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.Navigation
import com.example.finanstics.presentation.forms.Form
import com.example.finanstics.ui.theme.icons.CalendarIcon
import java.util.Calendar

@Suppress("MagicNumber", "LongMethod")
@Composable
fun FormAddData(
    value: String,
    label: String,
    isError: Boolean,
    lambda: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var formattedValue by remember { mutableStateOf(value) }

    val textFieldState = remember { mutableStateOf(TextFieldValue(formattedValue)) }

    val onValueChange: (String) -> Unit = { input ->
        val digitsOnly = input.replace(Regex("\\D"), "")

        val chunks = digitsOnly.chunked(2)
        val formattedDate = when {
            digitsOnly.length <= 2 -> digitsOnly
            digitsOnly.length <= 4 -> chunks.joinToString(".")
            digitsOnly.length <= 8 -> chunks[0] + "." + chunks[1] + "." + digitsOnly.substring(4)
            else -> chunks[0] + "." + chunks[1] + "." + digitsOnly.substring(4).take(4)
        }

        formattedValue = formattedDate
        lambda(formattedDate)

        textFieldState.value = TextFieldValue(
            formattedDate,
            selection = TextRange(
                formattedDate.length,
                formattedDate.length
            )
        )
    }

    OutlinedTextField(
        value = textFieldState.value,
        onValueChange = { newValue ->
            onValueChange(newValue.text)
        },
        label = {
            Text(
                label,
                color = MaterialTheme.colorScheme.primary
            )
        },
        readOnly = false,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = CalendarIcon,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d.%02d.%d", dayOfMonth, month + 1, year)
                lambda(formattedDate)
                onValueChange(formattedDate)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

@Suppress("MagicNumber")
@Composable
fun Form2(value: String, label: String, isError: Boolean, lambda: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = lambda,
        label = {
            Text(
                label,
                color = MaterialTheme.colorScheme.primary
            )
        },
        readOnly = false,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    )
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod")
@Composable
fun TypeSelector(
    value: String,
    label: String,
    expanded: Boolean,
    typeActions: List<String>,
    onExpandChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = {
                Text(
                    label,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { interaction ->
                            if (interaction is PressInteraction.Release) {
                                onExpandChange(true)
                            }
                        }
                    }
                },
            colors = OutlinedTextFieldDefaults.colors()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            typeActions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onTypeSelected(item)
                        onExpandChange(false)
                    }
                )
            }
        }
    }
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawIdle(
    uiState: AddActionUiState.Idle,
    vm: AddActionViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 50.dp,
                start = 20.dp,
                end = 20.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val typeActions = listOf(ActionType.INCOME.label, ActionType.EXPENSE.label)
        TypeSelector(
            value = if (uiState.typeAction != ActionType.NULL) uiState.typeAction.label else "",
            label = "Тип действия",
            expanded = uiState.menuExpandedType,
            typeActions = typeActions,
            onExpandChange = { vm.updateUIState(newMenuExpandedType = it) },
            onTypeSelected = { selectedActionType ->
                vm.updateUIState(
                    newTypeAction = ActionType
                        .entries
                        .firstOrNull { it.label == selectedActionType }
                )
            }
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

        TypeSelector(
            value = uiState.category,
            label = "Категория",
            expanded = uiState.menuExpandedCategory,
            typeActions = uiState.allCategory,
            onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
            onTypeSelected = { vm.updateUIState(newCategory = it) }
        )

        Form(
            value = uiState.description,
            label = "Описание",
            isError = false,
            lambda = { vm.updateUIState(newDescription = it) }
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
            ) {
                Text(
                    text = "Добавить",
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawError(
    uiState: AddActionUiState.Error,
    vm: AddActionViewModel,
    error: Error
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 50.dp,
                start = 20.dp,
                end = 20.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val typeActions = listOf(ActionType.INCOME.label, ActionType.EXPENSE.label)
        TypeSelector(
            value = if (uiState.typeAction != ActionType.NULL) uiState.typeAction.label else "",
            label = "Тип действия",
            expanded = uiState.menuExpandedType,
            typeActions = typeActions,
            onExpandChange = { vm.updateUIState(newMenuExpandedType = it) },
            onTypeSelected = { selectedActionType ->
                vm.updateUIState(
                    newTypeAction =
                        ActionType.entries.firstOrNull { it.label == selectedActionType }
                )
            }
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

        TypeSelector(
            value = uiState.category,
            label = "Категория",
            expanded = uiState.menuExpandedCategory,
            typeActions = uiState.allCategory,
            onExpandChange = { vm.updateUIState(newMenuExpandedCategory = it) },
            onTypeSelected = { vm.updateUIState(newCategory = it) }
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
    }
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun AddAction(
    navController: NavController
) {
    val vm: AddActionViewModel = viewModel()
    when (val uiState = vm.uiState.collectAsState().value) {
        is AddActionUiState.Idle -> {
            DrawIdle(uiState, vm, navController)
        }
        is AddActionUiState.Error -> {
            DrawError(uiState, vm, uiState.error)
        }

        is AddActionUiState.Ok -> {}

        else -> {}
    }
}
