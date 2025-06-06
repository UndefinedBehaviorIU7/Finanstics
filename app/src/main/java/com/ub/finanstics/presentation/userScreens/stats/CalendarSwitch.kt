package com.ub.finanstics.presentation.userScreens.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.userScreens.calendar.MonthNameClass
import com.ub.finanstics.ui.theme.icons.LeftIcon
import com.ub.finanstics.ui.theme.icons.RightIcon

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun CalendarSwitch(
    calendar: CalendarClass,
    vm: StatsViewModel = viewModel(),
    dvm: DetailsViewModel
) {
    val data = calendar.getData()
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
                .clickable {
                    vm.lastMonth()
                    dvm.hideDetailedActions()
                    vm.fetchData()
                }
        )

        Button(
            onClick = { },
            modifier = Modifier
                .weight(0.45f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                text = MonthNameClass.str(data.getMonth())
            )
        }
        Button(
            onClick = { },
            modifier = Modifier
                .weight(0.25f),
            colors = ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                text = data.getYear().toString()
            )
        }

        Icon(
            imageVector = RightIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    vm.nextMonth()
                    dvm.hideDetailedActions()
                    vm.fetchData()
                }
        )
    }
}
