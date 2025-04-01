package com.example.finanstics.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.finanstics.ui.theme.DEGREES_MAX
import com.example.finanstics.ui.theme.EXPENSES
import com.example.finanstics.ui.theme.INCOMES
import com.example.finanstics.ui.theme.STATS_ANIMATE_DURATION
import com.example.finanstics.ui.theme.generateColdColor
import com.example.finanstics.ui.theme.generateWarmColor

fun statsColors(expenses: Boolean, cnt: Int): List<Color> {
    if (expenses) {
        while (cnt > ColorsExpenses.size)
            ColorsExpenses.add(generateWarmColor())
        return ColorsExpenses
    }
    while (cnt > ColorsIncomes.size)
        ColorsIncomes.add(generateColdColor())
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

@Suppress("MagicNumber")
@Composable
fun PieChart(
    data: List<Pair<String, Int>>,
    expenses: Boolean = true,
    radiusOuter: Dp = 100.dp,
    chartBarWidth: Dp = 25.dp,
    animDuration: Int = STATS_ANIMATE_DURATION,
) {
    val totalSum = data.sumOf { it.second }
    val floatValue = calculateFloatValues(data, totalSum)
    val colors = statsColors(
        expenses,
        data.size
    )
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
                    .rotate(-floatValue[0])
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
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun calculateFloatValues(
    data: List<Pair<String, Int>>,
    totalSum: Int
): List<Float> {
    return data.map { (_, value) -> DEGREES_MAX * value / totalSum.toFloat() }
}
