@file:Suppress("UNCHECKED_CAST")

package com.example.finanstics.presentation.calendar

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.stats.Divider
import com.example.finanstics.ui.theme.icons.CircleIcon
import com.example.finanstics.ui.theme.icons.LeftIcon
import com.example.finanstics.ui.theme.icons.RightIcon
import kotlinx.coroutines.launch

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
                        action = day.getActions(),
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
                    text = "${day.getDayMoney()}",
                    color = MaterialTheme.colorScheme.primary,
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
                .size(30.dp)
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
                .size(30.dp)
                .clickable { vm.lastMonth() }
        )
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
    Divider(
        stroke = 2.dp,
        space = 20.dp,
        after = 0.dp
    )
    LazyColumn(
        modifier = Modifier
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
            .padding(
                top = 50.dp,
                start = 20.dp,
                end = 20.dp
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Календарь финансов",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

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
