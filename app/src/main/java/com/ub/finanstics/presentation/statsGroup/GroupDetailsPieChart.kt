package com.ub.finanstics.presentation.statsGroup

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
import androidx.compose.runtime.DisposableEffect
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
import com.ub.finanstics.R
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.presentation.actionView.ApiActionView
import com.ub.finanstics.presentation.actionView.formatDate
import com.ub.finanstics.presentation.calendar.CalendarClass
import com.ub.finanstics.presentation.stats.animateDp
import com.ub.finanstics.presentation.stats.statsColors
import com.ub.finanstics.presentation.stats.statsLabelId
import com.ub.finanstics.ui.theme.Blue

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun GroupDetailsPieChart(
    vm: GroupDetailsViewModel,
    data: List<Pair<String, Int>>,
    date: CalendarClass,
    expenses: Boolean
) {
    LaunchedEffect(Unit) {
        vm.autoUpdate()
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.cancelUpdate()
        }
    }
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
                GroupDetailsPieChartItem(
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
fun GroupDetailsPieChartItem(
    data: Pair<String, Int>,
    type: Int,
    widthSize: Float,
    color: Color = Blue,
    chosen: Boolean = false,
    vm: GroupDetailsViewModel
) {
    var isAnimationPlayed by remember { mutableStateOf(false) }
    var showAction by remember { mutableStateOf(false) }
    val ownerName by vm.name.collectAsState()
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
                        text = value,
                        fontWeight = FontWeight.Medium,
                        fontSize = if (value.length < 5) 16.sp
                        else (16 - (value.length - 5) * 3).sp,
                        color = if (chosen) color else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(modifier = Modifier.heightIn(max = 1200.dp)) {
                val uiState by vm.uiState.collectAsState()
                when (uiState) {
                    is GroupDetailsUiState.Detailed -> {
                        val detailedState = uiState as GroupDetailsUiState.Detailed
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
                                            vm.getActionOwner(action.userId)
                                            showAction = true
                                            vm.viewAction(action)
                                        },
                                        color = color
                                    )
                                }
                            }
                        }
                    }

                    is GroupDetailsUiState.DetailedAction -> {
                        val detailedState = uiState as GroupDetailsUiState.DetailedAction
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
                                        onClick = { },
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
            if (uiState is GroupDetailsUiState.DetailedAction) {
                val detState = uiState as GroupDetailsUiState.DetailedAction
                BoxWithConstraints {
                    val width = maxWidth
                    ApiActionView(
                        action = detState.action,
                        category = detState.chosen,
                        isVisible = showAction,
                        onDismiss = {
                            showAction = false
                            vm.forgetActionOwner()
                            vm.hideAction()
                        },
                        modifier = Modifier
                            .width(width - 20.dp),
                        name = ownerName,
                        color = color
                    )
                }
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
                text = formatDate(action.date),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        com.ub.finanstics.presentation.stats.BarLen(
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
            fontSize = if (action.value.toString().length < 5) 16.sp
            else (16 - (action.value.toString().length - 5) * 3).sp,
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
