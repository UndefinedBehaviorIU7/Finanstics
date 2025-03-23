package com.example.finanstics.presentation.stats

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    radiusOuter: Dp = 100.dp,
    chartBarWidth: Dp = 25.dp,
    animDuration: Int = 1000,
) {
    val totalSum = data.sumOf { it.second }
    val floatValue = calculateFloatValues(data, totalSum)
    val colors = statsColors(expenses)
    val animationPlayed = rememberAnimationPlayed()
    val animateSize = animateChartSize(animationPlayed, radiusOuter, animDuration)
    val animateRotation = animateChartRotation(animationPlayed, animDuration)
    val animateTextSize = animateTextSize(animationPlayed, animDuration)
    var lastValue = 0f

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
private fun rememberAnimationPlayed(): Boolean {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    return animationPlayed
}

@Composable
private fun animateChartSize(
    animationPlayed: Boolean,
    radiusOuter: Dp,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

@Composable
private fun animateChartRotation(
    animationPlayed: Boolean,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

@Composable
private fun animateTextSize(
    animationPlayed: Boolean,
    animDuration: Int
): Float {
    return animateFloatAsState(
        targetValue = if (animationPlayed) 24f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            easing = LinearOutSlowInEasing
        )
    ).value
}

private fun calculateFloatValues(
    data: List<Pair<String, Int>>,
    totalSum: Int
): List<Float> {
    return data.map { (_, value) -> 360 * value / totalSum.toFloat() }
}
