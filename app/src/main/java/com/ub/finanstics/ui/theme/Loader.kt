package com.ub.finanstics.ui.theme

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Loader(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary
    )
}
