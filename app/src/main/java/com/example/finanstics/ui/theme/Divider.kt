package com.example.finanstics.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("MagicNumber")
@Composable
fun Divider(
    space: Dp,
    stroke: Dp,
    after: Dp = 10.dp,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    Spacer(modifier = Modifier.height(space))
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = stroke,
        color = color
    )
    Spacer(modifier = Modifier.height(after))
}
