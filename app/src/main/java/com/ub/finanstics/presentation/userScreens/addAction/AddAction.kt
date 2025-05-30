package com.ub.finanstics.presentation.userScreens.addAction

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.presentation.templates.Form
import com.ub.finanstics.presentation.templates.Loader
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import java.time.Instant
import java.time.ZoneId

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
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
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )

    if (showDatePicker) {
        Dialog(
            onDismissRequest = { showDatePicker = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .width(370.dp)
                    .wrapContentHeight()
            ) {
                val datePickerState = rememberDatePickerState()
                val density = LocalDensity.current
                val colors = DatePickerDefaults.colors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.secondary,
                    selectedYearContentColor = MaterialTheme.colorScheme.primary,
                    selectedYearContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedDayContentColor = MaterialTheme.colorScheme.background
                )

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentHeight()
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier
                            .heightIn(max = with(density) { 500.dp.toPx() }.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .width(370.dp),
                        title = {
                            Text(
                                text = stringResource(R.string.select_data),
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        headline = null,
                        colors = colors,
                        showModeToggle = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDatePicker = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(R.string.cancellation))
                        }
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val zonedDateTime = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                val formattedDate = String.format(
                                    "%02d.%02d.%04d",
                                    zonedDateTime.dayOfMonth,
                                    zonedDateTime.monthValue,
                                    zonedDateTime.year
                                )
                                lambda(formattedDate)
                                onValueChange(formattedDate)
                            }
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
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
            shape = RoundedCornerShape(15.dp),
            containerColor = MaterialTheme.colorScheme.onBackground,
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
                    allElements.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selected(item)
                                onExpandChange(false)
                            }
                        )

                        if (index < allElements.size - 1) {
                            Divider(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
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
            shape = RoundedCornerShape(15.dp),
            containerColor = MaterialTheme.colorScheme.onBackground,
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
                    allElements.forEachIndexed { index, item ->
                        val isChecked = selectedItems.contains(item)
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = null,
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

                        if (index < allElements.size - 1) {
                            Divider(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawIdle(
    uiState: AddActionUiState.Idle,
    vm: AddActionViewModel,
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1]
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${stringResource(R.string.type_action)} ${uiState.typeAction.label}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Selector(
        value = uiState.category,
        label = stringResource(R.string.category),
        expanded = uiState.menuExpandedCategory,
        allElements = uiState.allCategory,
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

    MultiTypeSelector(
        selectedItems = uiState.groups,
        label = stringResource(R.string.duplication_group),
        expanded = uiState.menuExpandedGroup,
        allElements = uiState.allGroup,
        onExpandChange = { vm.updateUIState(newMenuExpandedGroup = it) },
        onSelectionChanged = { vm.updateUIState(newGroups = it) },
        isError = false
    )

    Spacer(modifier = Modifier.height(5.dp))

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val width = maxWidth
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.width(width),
            onClick = { vm.addAction() },
            enabled = vm.validateIdle(uiState) == ErrorAddAction.OK
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(R.string.add_action),
                fontSize = 24.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawError(
    uiState: AddActionUiState.Error,
    vm: AddActionViewModel,
    error: ErrorAddAction
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1]
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${stringResource(R.string.type_action)} ${uiState.typeAction.label}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.error_add_to_server),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (error == ErrorAddAction.ERROR_ADD_DATA_SERVER) {
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            onClick = { vm.addAction() },
        ) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = stringResource(R.string.retry),
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            onClick = { vm.addActionOnlyLocally() },
        ) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = stringResource(R.string.add_only_locally),
                fontSize = 20.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawErrorLoad(
    uiState: AddActionUiState.ErrorLoad,
    vm: AddActionViewModel,
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = if (uiState.typeAction == ActionType.EXPENSE) ColorsExpenses[0]
        else ColorsIncomes[1]
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${stringResource(R.string.type_action)} ${uiState.typeAction.label}",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.error_load_group),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            vm.tryLoad(uiState.typeAction)
        },
    ) {
        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            text = stringResource(R.string.retry),
            fontSize = 20.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            vm.withoutGroups(uiState.typeAction, uiState.allCategory)
        },
    ) {
        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            text = stringResource(R.string.сontinue_without_groups),
            fontSize = 20.sp
        )
    }
}

@Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
@Composable
fun DrawSelect(
    vm: AddActionViewModel,
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
            .padding(top = 20.dp)
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
fun AddAction(
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

    val vm: AddActionViewModel = viewModel()
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
                DrawIdle(uiState, vm)
            }

            is AddActionUiState.Error -> {
                DrawError(uiState, vm, uiState.error)
            }

            is AddActionUiState.Ok -> {
                if (!hasNavigatedBack) {
                    hasNavigatedBack = true
                    navController.popBackStack()
                }
            }

            is AddActionUiState.SelectType -> {
                DrawSelect(vm)
            }

            is AddActionUiState.Loading -> {
                BoxWithConstraints {
                    val width = maxWidth
                    Loader(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(width / 3)
                    )
                }
            }

            is AddActionUiState.ErrorLoad -> {
                DrawErrorLoad(uiState, vm)
            }
        }
    }
}
