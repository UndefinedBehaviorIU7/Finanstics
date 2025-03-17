package com.example.finanstics.presentation.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Form (value: String, label: String, isError: Boolean, lambda: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = lambda,
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.background,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface
        ),
        readOnly = false,
        modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth()
    )
}
