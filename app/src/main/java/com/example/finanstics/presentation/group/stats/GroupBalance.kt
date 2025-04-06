package com.example.finanstics.presentation.group.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanstics.ui.theme.ALL_TIME

@Composable
fun GroupBalance(
    currentBalance: Int,
    period: String,
    totalBalance: Int
) {
    Row() {
        Column() {
            Row() {
                Text(
                    text = "Баланс за $period:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    textAlign = TextAlign.Right,
                    text = "$currentBalance",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (period != ALL_TIME) {
                Spacer(Modifier.height(16.dp))
                Row() {
                    Text(
                        text = "Баланс за $ALL_TIME: ",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        textAlign = TextAlign.Right,
                        text = "$totalBalance",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
