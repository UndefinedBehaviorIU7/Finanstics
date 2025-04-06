package com.example.finanstics.presentation.calendarGroup

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.calendar.CalendarUiState
import com.example.finanstics.presentation.calendar.CalendarViewModel
import com.example.finanstics.presentation.calendar.DrawCalendarWithAction
import com.example.finanstics.presentation.calendar.DrawCalendarWithoutAction

@Suppress("MagicNumber")
@Composable
fun drawGroup(
    name: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { },
            modifier = Modifier.weight(0.25f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = "<"
            )
        }
        Button(
            onClick = { },
            modifier = Modifier.weight(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = "иконка"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = name
            )
        }
        Button(
            onClick = { },
            modifier = Modifier.weight(0.25f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = "..."
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun Calendar(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val vm: CalendarViewModel = viewModel()

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
        drawGroup("qqq")
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
        }
    }
}