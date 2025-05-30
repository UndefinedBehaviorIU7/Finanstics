package com.ub.finanstics.presentation.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("MagicNumber")
@Composable
fun DoubleButton(
    modifier: Modifier,
    buttonText: String,
    navText: String,
    navigate: () -> Unit,
    action: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Button(
            onClick = action,
            contentPadding = PaddingValues(
                vertical = 10.dp,
                horizontal = 20.dp,
            ),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buttonText,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        TextButton(
            onClick = navigate,
            modifier = Modifier.padding(top = 7.dp)
        ) {
            Text(
                text = navText,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun Toggler(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    fontSize: TextUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                checkedThumbColor = MaterialTheme.colorScheme.background,
                uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                uncheckedBorderColor = MaterialTheme.colorScheme.tertiary,
                uncheckedTrackColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

@Composable
fun BackArrow(
    onClick: () -> Unit
) {
    IconButton(
        onClick = { onClick },
        modifier = Modifier.fillMaxHeight()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxSize()
        )
    }
}
