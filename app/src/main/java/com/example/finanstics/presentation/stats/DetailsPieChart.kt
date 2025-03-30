package com.example.finanstics.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanstics.ui.theme.Blue
import com.example.finanstics.ui.theme.STATS_ANIMATE_DURATION

@Suppress("MagicNumber")
@Composable
fun DetailsPieChart(
    data: List<Pair<String, Int>>,
    expenses: Boolean,
    animateDuration: Int = STATS_ANIMATE_DURATION
) {
    val colors = statsColors(expenses)
    val sumTotal = data.sumOf { it.second }

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
                color = MaterialTheme.colorScheme.primary
            )
        }
        data.forEachIndexed { index, (_, value) ->
            DetailsPieChartItem(
                data = Pair(data[index].first, value),
                widthSize = (sumTotal / data[index].second.toFloat()),
                color = colors[index],
                animateDuration = animateDuration
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    widthSize: Float,
    color: Color = Blue,
    animateDuration: Int = STATS_ANIMATE_DURATION
) {
    var isAnimationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimationPlayed = true
    }
    println(widthSize)

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
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            BoxWithConstraints(modifier = Modifier.weight(4f)) {
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
                        .height(10.dp)
                        .width(barLen)
                )
            }

            Column(modifier = Modifier.weight(2f)) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.second.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
