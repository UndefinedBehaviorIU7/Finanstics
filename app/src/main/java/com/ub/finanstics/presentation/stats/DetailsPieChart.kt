package com.ub.finanstics.presentation.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ub.finanstics.R
import com.ub.finanstics.db.Action
import com.ub.finanstics.presentation.actionView.LocalActionView
import com.ub.finanstics.presentation.calendar.CalendarClass
import com.ub.finanstics.ui.theme.Blue
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun DetailsPieChart(
    vm: DetailsViewModel,
    data: List<Pair<String, Int>>,
    date: CalendarClass,
    expenses: Boolean
) {
    val colors = statsColors(
        expenses,
        data.size
    )
    val sumTotal = data.sumOf { it.second }
    vm.date = date

    val chosen by vm.chosenCategory.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(statsLabelId(expenses)),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (data.isNotEmpty()) {
            data.forEachIndexed { index, (_, value) ->
                val type = if (expenses) 0 else 1
                DetailsPieChartItem(
                    data = Pair(data[index].first, value),
                    widthSize = (sumTotal / data[index].second.toFloat()),
                    color = colors[index],
                    vm = vm,
                    type = type,
                    chosen = data[index].first == chosen.first && type == chosen.second
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.absent),
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongParameterList", "LongMethod")
@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    type: Int,
    widthSize: Float,
    color: Color = Blue,
    chosen: Boolean = false,
    vm: DetailsViewModel
) {
    var isAnimationPlayed by remember { mutableStateOf(false) }
    var showAction by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isAnimationPlayed = true }

    Surface(
        modifier = Modifier.padding(vertical = 20.dp),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vm.changeState(data.first, type) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        modifier = Modifier.padding(end = 15.dp),
                        text = data.first,
                        fontWeight = if (chosen) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp,
                        color = if (chosen) color else MaterialTheme.colorScheme.primary
                    )
                }

                BarLen(
                    modifier = Modifier.weight(4f),
                    isAnimationPlayed = isAnimationPlayed,
                    widthSize = widthSize,
                    color = color
                )
                val value = data.second.toString()
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = data.second.toString(),
                        fontWeight = FontWeight.Medium,
                        fontSize = if (value.length < 6) 16.sp
                        else (16 - (value.length - 6) * 3).sp,
                        color = if (chosen) color else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(modifier = Modifier.heightIn(max = 1200.dp)) {
                val uiState by vm.uiState.collectAsState()
                when (uiState) {
                    is DetailsUiState.Detailed -> {
                        val detailedState = uiState as DetailsUiState.Detailed
                        if (detailedState.chosen == data.first && detailedState.type == type) {
                            LazyColumn(
                                modifier = Modifier.padding(top = 5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(detailedState.actions) { action ->
                                    ActionInfo(
                                        action = action,
                                        totalSum = data.second,
                                        widthSize = widthSize,
                                        onClick = {
                                            showAction = true
                                            vm.viewAction(action)
                                        },
                                        color = color
                                    )
                                }
                            }
                        }
                    }

                    is DetailsUiState.DetailedAction -> {
                        val detailedState = uiState as DetailsUiState.DetailedAction
                        if (detailedState.chosen == data.first && detailedState.type == type) {
                            LazyColumn(
                                modifier = Modifier.padding(top = 5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(detailedState.actions) { action ->
                                    ActionInfo(
                                        action = action,
                                        totalSum = data.second,
                                        widthSize = widthSize,
                                        onClick = {
                                            showAction = true
                                            vm.viewAction(action)
                                        },
                                        color = color
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            val uiState by vm.uiState.collectAsState()
            if (uiState is DetailsUiState.DetailedAction) {
                val detState = uiState as DetailsUiState.DetailedAction
                LocalActionView(
                    action = detState.action,
                    category = detState.chosen,
                    isVisible = showAction,
                    onDismiss = {
                        showAction = false
                        vm.hideAction()
                    },
                    modifier = Modifier
                        .width(380.dp)
                        .height(250.dp),
                    color = color
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("MagicNumber")
fun ActionInfo(
    action: Action,
    totalSum: Int,
    widthSize: Float,
    onClick: () -> Unit,
    color: Color
) {
    var isAnimationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimationPlayed = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(3f)) {
            Text(
                text = action.name,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dateToString(action.date),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        BarLen(
            modifier = Modifier.weight(4f),
            widthSize = totalSum.toFloat() / (action.value.toFloat() / widthSize),
            isAnimationPlayed = true,
            color = color,
            colorIn = MaterialTheme.colorScheme.background
        )

        Text(
            modifier = Modifier
                .weight(2f)
                .padding(start = 15.dp),
            text = action.value.toString(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
@Suppress("MagicNumber")
fun BarLen(
    modifier: Modifier,
    isAnimationPlayed: Boolean = true,
    widthSize: Float,
    color: Color,
    colorIn: Color = color,
) {
    BoxWithConstraints(modifier = modifier) {
        val barLen = animateDp(
            animationPlayed = isAnimationPlayed,
            start = 0.dp,
            end = maxWidth / widthSize,
            animDuration = 1000
        )
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(2.dp)
                .background(
                    color = colorIn,
                    shape = RoundedCornerShape(8.dp)
                )
                .height(10.dp)
                .width(barLen)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun dateToString(
    date: LocalDate
): String {
    var day = "${date.dayOfMonth}"
    if (day.length == 1) {
        day = "0$day"
    }

    var month = "${date.monthValue}"
    if (month.length == 1) {
        month = "0$month"
    }

    return "$day.$month.${date.year}"
}
