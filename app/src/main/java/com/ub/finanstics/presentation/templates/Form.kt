package com.ub.finanstics.presentation.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("MagicNumber", "LongParameterList")
@Composable
fun Form(
    value: String,
    label: String,
    isError: Boolean,
    lambda: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = lambda,
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        readOnly = false,
        modifier = modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        trailingIcon = { icon() }
    )
}

@Suppress("MagicNumber", "LongParameterList")
@Composable
fun PasswordForm(
    value: String,
    label: String,
    isError: Boolean,
    lambda: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = lambda,
        label = { Text(label) },
        visualTransformation = PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isError)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        readOnly = false,
        modifier = modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        trailingIcon = { icon() }
    )
}
