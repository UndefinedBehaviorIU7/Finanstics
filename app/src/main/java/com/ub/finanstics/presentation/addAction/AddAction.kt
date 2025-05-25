package com.ub.finanstics.presentation.addAction

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.presentation.forms.Form
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import com.ub.finanstics.ui.theme.icons.CalendarIcon
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
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        readOnly = false,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = CalendarIcon,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
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

@Suppress("MagicNumber", "LongParameterList", "LongMethod")
@Composable
fun Selector(
    value: String,
    label: String,
    expanded: Boolean,
    isError: Boolean,
    allElements: List<String>,
    onExpandChange: (Boolean) -> Unit,
    selected: (String) -> Unit
) {
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},

            label = {
                Text(
                    text = label
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
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },
            shape = RoundedCornerShape(15.dp),
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
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .heightIn(max = screenHeight * 0.3f)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.3f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    allElements.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selected(item)
                                onExpandChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun MultiTypeSelector(
    selectedItems: List<Group>,
    label: String,
    expanded: Boolean,
    allElements: List<Group>,
    onExpandChange: (Boolean) -> Unit,
    onSelectionChanged: (List<Group>) -> Unit,
    isError: Boolean = false
) {
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        OutlinedTextField(
            value = if (selectedItems.isEmpty()) "" else selectedItems.joinToString { it.name },
            onValueChange = {},
            label = {
                Text(text = label)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.secondary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },
            shape = RoundedCornerShape(15.dp),
            interactionSource = interactionSource
        )

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    onExpandChange(true)
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .heightIn(max = screenHeight * 0.3f)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.3f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    allElements.forEach { item ->
                        val isChecked = selectedItems.contains(item)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(item.name)
                                }
                            },
                            onClick = {
                                val newSelection = if (isChecked) {
                                    selectedItems - item
                                } else {
                                    selectedItems + item
                                }
                                onSelectionChanged(newSelection)
                            }
                        )
                    }
                }
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
    Divider(
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1],
        thickness = 1.dp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Тип действия: ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
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

    MultiTypeSelector(
        selectedItems = uiState.groups,
        label = "Продублировать в группы",
        expanded = uiState.menuExpandedGroup,
        allElements = uiState.allGroup,
        onExpandChange = { vm.updateUIState(newMenuExpandedGroup = it) },
        onSelectionChanged = { vm.updateUIState(newGroups = it) },
        isError = false
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

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawError(
    uiState: AddActionUiState.Error,
    vm: AddActionViewModel,
    error: Error
) {
    Divider(
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1],
        thickness = 1.dp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Тип действия: ${ uiState.typeAction.label }",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
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
        label = "Продублировать в группы",
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
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun AddAction(
    navController: NavController
) {
    val vm: AddActionViewModel = viewModel()
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

        when (val uiState = vm.uiState.collectAsState().value) {
            is AddActionUiState.Idle -> {
                DrawIdle(uiState, vm, navController)
            }

            is AddActionUiState.Error -> {
                DrawError(uiState, vm, uiState.error)
            }

            is AddActionUiState.Ok -> {}

            is AddActionUiState.SelectType -> {

                Divider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выберете тип действия",
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
                        onClick = { vm.chooseTypeAndLoad(ActionType.EXPENSE) },
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
                        onClick = { vm.chooseTypeAndLoad(ActionType.INCOME) },
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

            else -> {}
        }
    }
}
