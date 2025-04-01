@file:Suppress("UNCHECKED_CAST")

package com.example.finanstics.presentation.calendar

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Suppress("MagicNumber")
@Composable
fun CalendarDay(
    days: Array<DayClass?>,
    vm: CalendarViewModel
) {
    val columns = 7

    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        (days.toList()).chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                rowItems.forEach { day ->
                    if (day != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            Button(
                                onClick = { vm.actions(day.getActions()) },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    Text(
                                        text = "${day.getDayData()}",
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        text = "${day.getDayMoney()}",
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
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

@Suppress("MagicNumber")
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
        Button(
            onClick = { vm.lastMonth() },
            modifier = Modifier
                .weight(0.15f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = "<"
            )
        }
        Button(
            onClick = { /* Действие при нажатии */ },
            modifier = Modifier
                .weight(0.45f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background,
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
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = year.toString()
            )
        }
        Button(
            onClick = { vm.nextMonth() },
            modifier = Modifier
                .weight(0.15f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = ">"
            )
        }
    }

    WeekDraw()

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Suppress("MagicNumber")
@Composable
fun CalendarDraw(
    calendar: CalendarClass,
    vm: CalendarViewModel
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onBackground)
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
    action: Action
) {
    Button(
        onClick = { },
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
                    text = action.getActionName(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = action.getUserName(),
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
                    text = "${action.getActionType()}",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${action.getMoney()}",
                    color = MaterialTheme.colorScheme.primary,
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
    actions: Array<Action?>
) {
    LazyColumn(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(actions.size) { index ->
            val action = actions[index]
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

@Suppress("MagicNumber")
@Composable
fun Calendar(
    navController: NavController
) {
    val context = LocalContext.current
    val vm: CalendarViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CalendarViewModel(
                    application = context.applicationContext as Application
                ) as T
            }
        }
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Календарь финансов")

        when (val uiState = vm.uiState.collectAsState().value) {
            is CalendarUiState.Idle -> {
            }
            is CalendarUiState.Loading -> {
            }
            is CalendarUiState.Error -> {
                Text("Error: ${uiState.message}")
            }
            is CalendarUiState.Default -> {
                CalendarDraw(uiState.calendar, vm)
            }

            is CalendarUiState.DrawActions -> {
                CalendarDraw(uiState.calendar, vm)
                ActionsDraw(uiState.actions.toTypedArray())
            }
        }
    }
}
