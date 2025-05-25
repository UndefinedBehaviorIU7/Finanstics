@file:Suppress("UNCHECKED_CAST")

package com.ub.finanstics.presentation.calendar

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import com.ub.finanstics.ui.theme.Divider
import com.ub.finanstics.ui.theme.icons.LeftIcon
import com.ub.finanstics.ui.theme.icons.RightIcon

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun CalendarDay(
    days: Array<DayClass?>,
    vm: CalendarViewModel
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
                    is CalendarUiState.DrawActions -> {
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
    vm: CalendarViewModel,
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
                containerColor = MaterialTheme.colorScheme.onBackground,
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

                Text(
                    text = "${kotlin.math.abs(day.getDayMoney())}",
                    color = if (day.getDayMoney() < 0) ColorsExpenses[0]
                    else if (day.getDayMoney() > 0) ColorsIncomes[1]
                            else MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun WeekDraw() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun CalendarHeading(
    month: MonthNameClass,
    year: Int,
    vm: CalendarViewModel
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
    vm: CalendarViewModel
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

@Suppress("MagicNumber", "LongMethod")
@Composable
fun DrawAction(
    actionDataClass: ActionDataClass
) {
    Button(
        onClick = {  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.primary
        ),
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

@Suppress("MagicNumber")
@Composable
fun ActionsDraw(
    actionDataClasses: Array<ActionDataClass?>
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
                    DrawAction(action)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun DrawCalendarWithAction(
    calendar: CalendarClass,
    actionDataClasses: Array<ActionDataClass?>,
    isLandscape: Boolean,
    vm: CalendarViewModel,
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
                ActionsDraw(actionDataClasses)
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
            after = 0.dp
        )
        ActionsDraw(actionDataClasses)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun DrawCalendarWithoutAction(
    calendar: CalendarClass,
    isLandscape: Boolean,
    vm: CalendarViewModel,
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
            text = "Календарь финансов",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp
        )
        CalendarDraw(calendar, vm)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun Calendar(
    navController: NavController,
    isVisible: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val vm: CalendarViewModel = viewModel()

    LaunchedEffect(Unit) { }

    DisposableEffect(Unit) {
        onDispose { }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 20.dp,
                start = 20.dp,
                end = 20.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val uiState = vm.uiState.collectAsState().value) {
            is CalendarUiState.Idle -> {
            }

            is CalendarUiState.Loading -> {
            }

            is CalendarUiState.Error -> {
                Text("Error: ${uiState.message}")
            }

            is CalendarUiState.Default -> {
                DrawCalendarWithoutAction(uiState.calendar, isLandscape, vm)
            }

            is CalendarUiState.DrawActions -> {
                val action = uiState.day?.getActions()
                if (action != null) {
                    DrawCalendarWithAction(uiState.calendar, action, isLandscape, vm)
                }
            }

            else -> {}
        }
    }
}
