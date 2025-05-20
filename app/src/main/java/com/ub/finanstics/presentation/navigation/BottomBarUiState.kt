package com.ub.finanstics.presentation.navigation

import androidx.compose.ui.unit.Dp

sealed class BottomBarUiState {
    object Hidden : BottomBarUiState()

    data class Visible(
        val offset: Dp
    ) : BottomBarUiState()
}
