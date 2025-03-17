package com.example.finanstics.presentation.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonForm(modifier: Modifier, buttonText: String, navText: String,
               navigate: () -> Unit, action: () -> Unit) {
    Column (horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier) {
        Button(
            onClick = action,
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 10.dp,
                start = 20.dp,
                end = 20.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text (
                text = buttonText,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        TextButton(
            onClick = navigate,
            modifier = Modifier.padding(top = 7.dp)
        ) {
            Text(
                text = navText,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
