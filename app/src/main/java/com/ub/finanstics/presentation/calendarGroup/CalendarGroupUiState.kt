package com.ub.finanstics.presentation.calendarGroup

import android.graphics.Bitmap
import com.ub.finanstics.presentation.calendar.CalendarClass
import com.ub.finanstics.presentation.calendar.DayClass

sealed class CalendarGroupUiState {
    data object Idle : CalendarGroupUiState()
    data object Loading : CalendarGroupUiState()

    data class Error(
        val message: String
    ) : CalendarGroupUiState()

    data class Default(
        val calendar: CalendarClass,
    ) : CalendarGroupUiState()

    data class DrawActions(
        val calendar: CalendarClass,
        val day: DayClass?
    ) : CalendarGroupUiState()

    data class DrawActionDetail(
        val calendar: CalendarClass,
        val day: DayClass?,
        val action: com.ub.finanstics.api.models.Action,
        val category: String,
        val type: Int,
        val name: String,
        val imageBitmap: Bitmap? = null,
    ) : CalendarGroupUiState()
}

