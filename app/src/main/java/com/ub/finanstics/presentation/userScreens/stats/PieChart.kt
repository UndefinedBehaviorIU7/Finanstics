package com.ub.finanstics.presentation.userScreens.stats

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
import com.ub.finanstics.R
import com.ub.finanstics.ui.theme.ColorsExpenses
import com.ub.finanstics.ui.theme.ColorsIncomes
import com.ub.finanstics.ui.theme.generateColdColor
import com.ub.finanstics.ui.theme.generateWarmColor

const val DEGREES_MAX = 360f
const val STATS_ANIMATE_DURATION = 1000

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

fun statsLabelId(expenses: Boolean): Int {
    if (expenses) return R.string.expenses
    return R.string.incomes
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun PieChart(
    data: List<Pair<String, Int>>,
    expenses: Boolean = true,
    radiusOuter: Dp = 100.dp,
    chartBarWidth: Dp = 25.dp,
    animDuration: Int = STATS_ANIMATE_DURATION,
) {
    val totalSum = data.sumOf { it.second }
    val floatValue = calculateFloatValues(data, totalSum).toMutableList()

    if (floatValue.isNotEmpty() && floatValue[0] == 0f) {
        floatValue[0] = DEGREES_MAX
    }
    val colors = statsColors(
        expenses,
        data.size
    )
    val backColor = MaterialTheme.colorScheme.onBackground
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
                    .rotate(if (floatValue.isNotEmpty()) -floatValue[0] else 0f)
                    .rotate(animateRotation)
            ) {
                if (floatValue.isEmpty()) {
                    drawArc(
                        color = backColor,
                        0f,
                        360f,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                } else {
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
    if (totalSum <= 0) return emptyList()

    return data.map { (_, value) ->
        val safeValue = value.coerceAtLeast(0)
        (DEGREES_MAX * safeValue / totalSum.toFloat()).coerceIn(0f, DEGREES_MAX.toFloat())
    }
}

