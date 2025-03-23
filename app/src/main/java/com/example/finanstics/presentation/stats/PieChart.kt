package com.example.finanstics.presentation.stats

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanstics.ui.theme.Blue
import com.example.finanstics.ui.theme.ColorsExpenses
import com.example.finanstics.ui.theme.ColorsIncomes
import com.example.finanstics.ui.theme.EXPENSES
import com.example.finanstics.ui.theme.INCOMES

fun statsColors(expenses: Boolean): List<Color> {
    if (expenses) return ColorsExpenses
    return ColorsIncomes
}

fun sumToSignText(sum: Int, expense: Boolean): String {
    val textSum = sum.toString()
    val textSumSign = if (expense) {
        "-$textSum"
    } else {
        "+$textSum"
    }
    return textSumSign
}

fun statsLabel(expenses: Boolean): String {
    if (expenses) return EXPENSES
    return INCOMES
}

@Composable
fun PieChart(
    data: List<Pair<String, Int>>,
    expenses: Boolean = true,
    radiusOuter: Dp = 50.dp,
    chartBarWidth: Dp = 35.dp,
    animDuration: Int = 1000,
) {
    val totalSum = data.sumOf { it.second }
    val floatValue = mutableListOf<Float>()

    data.forEachIndexed { index, (_, values) ->
        floatValue.add(index, 360 * values / totalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f
    val colors = statsColors(expenses)

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )
    val animateTextSize by animateFloatAsState(
        targetValue = if (animationPlayed) 24f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(radiusOuter * 2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(animateSize.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
            Text(
                text = sumToSignText(totalSum, expenses),
                fontSize = animateTextSize.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DetailsPieChart(
    data: List<Pair<String, Int>>,
    expenses: Boolean
) {
    val colors = statsColors(expenses)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = statsLabel(expenses),
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                color = Color.Black
            )
        }
        data.forEachIndexed { index, (_, value) ->
            DetailsPieChartItem(
                data = Pair(data[index].first, value),
                widthSize = ((data.sumOf { it.second }) / data[index].second).toFloat(),
                color = colors[index]
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    widthSize: Float,
    color: Color = Blue
) {
    Surface(
        modifier = Modifier.padding(vertical = 10.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    modifier = Modifier.padding(end = 15.dp),
                    text = data.first,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            BoxWithConstraints(modifier = Modifier.weight(4f)) {
                Box(
                    modifier = Modifier
                        .background(
                            color = color,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .height(10.dp)
                        .width(maxWidth / widthSize)
                )
            }

            Column(modifier = Modifier.weight(2f)) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.second.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = color
                )
            }
        }
    }
}

private val incomes = listOf(
    "Зарплата" to 10000,
    "Стипендия" to 5500,
    "Переводы" to 2000,
)

private val expenses = listOf(
    "Покупки" to 16500,
    "Налоги/штрафы" to 7400,
    "Еда" to 4000,
    "Развлечения" to 2200,
    "Транспорт" to 1900
)

@Preview
@Composable
fun PieChartExample() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        item {
            Row() {
                Column(modifier = Modifier.weight(1f)) {
                    PieChart(
                        data = incomes,
                        radiusOuter = 90.dp,
                        expenses = false,
                        chartBarWidth = 26.dp,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    PieChart(
                        data = expenses,
                        radiusOuter = 90.dp,
                        expenses = true,
                        chartBarWidth = 26.dp,
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            DetailsPieChart(
                data = incomes,
                expenses = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            DetailsPieChart(
                data = expenses,
                expenses = true
            )
        }
    }
}
