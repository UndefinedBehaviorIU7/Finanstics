package com.ub.finanstics.presentation.statsGroup

import android.graphics.Bitmap
import com.ub.finanstics.api.models.Action

sealed class GroupDetailsUiState {
    object Default : GroupDetailsUiState()

    data class Detailed(
        val actions: List<Action>,
        val chosen: String,
        val type: Int
    ) : GroupDetailsUiState()

    data class DetailedAction(
        val actions: List<Action>,
        val chosen: String,
        val action: Action,
        val ownerName: String,
        val imageBitmap: Bitmap?,
        val type: Int
    ) : GroupDetailsUiState()
}
