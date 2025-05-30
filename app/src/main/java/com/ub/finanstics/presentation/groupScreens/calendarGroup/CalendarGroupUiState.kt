package com.ub.finanstics.presentation.groupScreens.calendarGroup

import android.graphics.Bitmap
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.userScreens.calendar.DayClass
import com.ub.finanstics.presentation.userScreens.calendar.ErrorCalendar

sealed class CalendarGroupUiState {
    data object Idle : CalendarGroupUiState()
    data object Loading : CalendarGroupUiState()

    data class Error(
        val message: ErrorCalendar
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
        val action: Action,
        val category: String,
        val type: Int,
        val name: String,
        val imageBitmap: Bitmap? = null,
    ) : CalendarGroupUiState()
}

