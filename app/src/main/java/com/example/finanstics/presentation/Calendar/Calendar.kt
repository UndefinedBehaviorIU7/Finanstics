@file:Suppress("UNREACHABLE_CODE")

package com.example.calendar

import android.app.Application
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calendar.DayClass.Companion.DayOfWeekInit
import java.time.DayOfWeek
import java.util.Calendar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendar.CalendarUiState

@Composable
fun draw_Day(Day: DayClass)
{

}

@Composable
fun CalendarDay(Days: Array<DayClass?>) {
    val columns = 7

    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        (Days.toList()).chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                rowItems.forEach { Day ->
                    if (Day != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            Button(
                                onClick = { /* Действие при нажатии */ },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    Color.White,
                                    Color.Black
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(1.dp)
                                )
                                {

                                    Text(
                                        text = "${Day.day}",
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        text = "${Day.money}",
                                        color = Color.Black,
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



@Composable
fun CalendarHeading(month: MonthNameClass, year: Int, Days: Array<DayClass?>, vm : CalendarViewModel) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Календарь финансов")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF1F0ED)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { vm.previousMonth() },
                    modifier = Modifier
                        .weight(0.15f),
                    colors = ButtonDefaults.buttonColors(
                        Color.White, Color.Black
                    )
                ) {
                    Text(text = "<")
                }
                Button(
                    onClick = { /* Действие при нажатии */ },
                    modifier = Modifier
                        .weight(0.45f),
                    colors = ButtonDefaults.buttonColors(
                        Color.White, Color.Black
                    )
                ) {
                    Text(text = MonthNameClass.str(month))
                }
                Button(
                    onClick = { /* Действие при нажатии */ },
                    modifier = Modifier
                        .weight(0.25f), // Увеличиваем кнопку года
                    colors = ButtonDefaults.buttonColors(
                        Color.White, Color.Black
                    )
                ) {
                    Text(text = year.toString())
                }
                Button(
                    onClick = { vm.nextMonth() },
                    modifier = Modifier
                        .weight(0.15f),
                    colors = ButtonDefaults.buttonColors(
                        Color.White, Color.Black
                    )
                ) {
                    Text(text = ">")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Divider(
                color = Color.Black,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            CalendarDay(Days)
        }
    }
}

@Composable
fun Calendar() {
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
    when (val uiState = vm.uiState.collectAsState().value) {
        is CalendarUiState.Idle -> {

        }
        is CalendarUiState.Loading -> {

        }
        is CalendarUiState.Error -> {
            Text("Error: ${uiState.message}")
        }
        is CalendarUiState.Success -> {
            CalendarHeading(uiState.calendar.month, uiState.calendar.year, uiState.calendar.Days, vm)
        }
    }
}
