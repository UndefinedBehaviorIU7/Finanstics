@file:Suppress("UNCHECKED_CAST")

package com.example.finanstics.presentation.calendar

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
import com.example.finanstics.presentation.stats.Divider
import com.example.finanstics.ui.theme.icons.LeftIcon
import com.example.finanstics.ui.theme.icons.RightIcon

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
                    color = if (day.getDayMoney() < 0) Color.Red else Color.Green,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
    }
}

//@Suppress("MagicNumber")
//@Composable
//fun WeekDraw() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
//            Text(
//                color = MaterialTheme.colorScheme.primary,
//                text = day,
//                modifier = Modifier.weight(1f),
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}

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
fun DrawCalendarWithAction(
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
                ActionsDraw(actionDataClasses)
            }
        }
    } else {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Календарь группы",
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
            text = "Календарь группы",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp
        )
        CalendarDraw(calendar, vm)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun CalendarGroup(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val vm: CalendarGroupViewModel = viewModel()

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
            is CalendarGroupUiState.Idle -> {
            }

            is CalendarGroupUiState.Loading -> {
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
                    DrawCalendarWithAction(uiState.calendar, action, isLandscape, vm)
                }
            }

            else -> {}
        }
    }
}
