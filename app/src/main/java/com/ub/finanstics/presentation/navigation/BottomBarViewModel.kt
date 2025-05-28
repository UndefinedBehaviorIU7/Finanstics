package com.ub.finanstics.presentation.navigation

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomBarViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<BottomBarUiState>(BottomBarUiState.Hidden)
    val uiState = _uiState.asStateFlow()

    private val _blocked = MutableStateFlow(false)
    val blocked = _blocked.asStateFlow()

    fun hide() {
        if (uiState.value is BottomBarUiState.Visible) {
            _uiState.value = BottomBarUiState.Hidden
        }
    }

    fun show(offset: Dp) {
        if (uiState.value is BottomBarUiState.Hidden) {
            _uiState.value = BottomBarUiState.Visible(offset)
        }
    }

    fun block() {
        _blocked.value = true
    }

    fun unblock() {
        _blocked.value = false
    }
}
