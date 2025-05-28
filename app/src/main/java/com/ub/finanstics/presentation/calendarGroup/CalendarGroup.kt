package com.ub.finanstics.presentation.calendar

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ub.finanstics.R
import com.ub.finanstics.presentation.actionView.ApiActionView
import com.ub.finanstics.presentation.templates.Divider
import com.ub.finanstics.presentation.templates.Loader
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import com.ub.finanstics.ui.theme.OFFSET_BAR
import com.ub.finanstics.ui.theme.averageColor
import com.ub.finanstics.ui.theme.icons.LeftIcon
import com.ub.finanstics.ui.theme.icons.RightIcon
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun DrawActionGroup(
    actionDataClass: ActionDataClass,
    vm: CalendarGroupViewModel
) {
    Button(
        onClick = { vm.viewAction(action = actionDataClass) },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = actionDataClass.getActionName(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = actionDataClass.getActionCategory(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${actionDataClass.getMoney()}",
                    color = if (actionDataClass.getActionType() == 0) ColorsExpenses[0]
                    else ColorsIncomes[1],
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun ActionsGroupDraw(
    actionDataClasses: Array<ActionDataClass?>,
    vm: CalendarGroupViewModel
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(actionDataClasses.size) { index ->
            val action = actionDataClasses[index]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                if (action != null) {
                    DrawActionGroup(action, vm)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(OFFSET_BAR + 50.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun DrawCalendarGroupWithAction(
    calendar: CalendarClass,
    actionDataClasses: Array<ActionDataClass?>,
    isLandscape: Boolean,
    vm: CalendarGroupViewModel,
) {
    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                CalendarDraw(calendar, vm)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                ActionsGroupDraw(actionDataClasses, vm)
            }
        }
    } else {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Календарь финансов",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp
        )
        CalendarDraw(calendar, vm)
        Divider(
            stroke = 2.dp,
            space = 20.dp,
            after = 10.dp
        )
        ActionsGroupDraw(actionDataClasses, vm)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun CalendarDay(
    days: Array<DayClass?>,
    vm: CalendarGroupViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(days.size) { index ->
            val day = days[index]
            var selected = false
            if (day != null) {
                when (val uiState = vm.uiState.collectAsState().value) {
                    is CalendarGroupUiState.DrawActions -> {
                        if (day == uiState.day)
                            selected = true
                    }

                    else -> selected = false
                }
                CalendarDayItem(
                    day = day,
                    vm = vm,
                    selected = selected
                )
            } else {
                Spacer(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
private fun CalendarDayItem(
    day: DayClass,
    vm: CalendarGroupViewModel,
    selected: Boolean = false
) {
    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (selected) {
                    vm.toDefault()
                } else {
                    vm.actions(
                        day = day
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (day.getDayMonth() == vm.getCalendarMonth())
                    MaterialTheme.colorScheme.onBackground
                else averageColor(listOf(MaterialTheme.colorScheme.onBackground,
                    MaterialTheme.colorScheme.background)),
                contentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${day.getDayData()}",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                val value = abs(day.getDayMoney()).toString()
                Text(
                    text = "${abs(day.getDayMoney())}",
                    color = if (day.getDayMoney() < 0) ColorsExpenses[0]
                    else if (day.getDayMoney() > 0) ColorsIncomes[1]
                    else MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontSize = if (value.length < 5) 12.sp
                    else (12 - (value.length - 5) * 3).sp
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun CalendarHeading(
    month: MonthNameClass,
    year: Int,
    vm: CalendarGroupViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = LeftIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .clickable { vm.lastMonth() }
        )

        Button(
            onClick = { /* Действие при нажатии */ },
            modifier = Modifier
                .weight(0.45f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = MonthNameClass.str(month)
            )
        }
        Button(
            onClick = { /* Действие при нажатии */ },
            modifier = Modifier
                .weight(0.25f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = year.toString()
            )
        }

        Icon(
            imageVector = RightIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .clickable { vm.nextMonth() }
        )
    }

    WeekDraw()

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun CalendarDraw(
    calendar: CalendarClass,
    vm: CalendarGroupViewModel
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val data = calendar.getData()
        CalendarHeading(data.getMonth(), data.getYear(), vm)
        CalendarDay(calendar.getDays(), vm)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun DrawCalendarWithoutAction(
    calendar: CalendarClass,
    isLandscape: Boolean,
    vm: CalendarGroupViewModel,
) {
    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                CalendarDraw(calendar, vm)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
            }
        }
    } else {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.calendar_group),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp
        )
        CalendarDraw(calendar, vm)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun CalendarGroup(
    isVisible: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val vm: CalendarGroupViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(isVisible) {
        if (isVisible) {
            vm.autoUpdate()
        } else {
            vm.cancelUpdate()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.cancelUpdate()
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(
                top = 20.dp,
                start = 20.dp,
                end = 20.dp
            )
            .fillMaxSize()
            .blur(
                if (state is CalendarGroupUiState.DrawActionDetail) 10.dp else 0.dp,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val uiState = vm.uiState.collectAsState().value) {
            is CalendarGroupUiState.Idle -> {
            }

            is CalendarGroupUiState.Loading -> {
                BoxWithConstraints {
                    val width = maxWidth
                    Loader(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(width / 3)
                    )
                }
            }

            is CalendarGroupUiState.Error -> {
                Text("Error: ${uiState.message}")
            }

            is CalendarGroupUiState.Default -> {
                DrawCalendarWithoutAction(uiState.calendar, isLandscape, vm)
            }

            is CalendarGroupUiState.DrawActions -> {
                val action = uiState.day?.getActions()
                if (action != null) {
                    DrawCalendarGroupWithAction(uiState.calendar, action, isLandscape, vm)
                }
            }

            is CalendarGroupUiState.DrawActionDetail -> {
                val action = uiState.day?.getActions()
                if (action != null) {
                    DrawCalendarGroupWithAction(uiState.calendar, action, isLandscape, vm)
                }
                ApiActionView(
                    action = uiState.action,
                    category = uiState.category,
                    isVisible = true,
                    onDismiss = {
                        vm.hideAction()
                    },
                    modifier = Modifier
                        .width(380.dp)
                        .height(250.dp),
                    color = if (uiState.type == 0) ColorsExpenses[0]
                    else ColorsIncomes[1]
                )
            }

            else -> {}
        }
    }
}
