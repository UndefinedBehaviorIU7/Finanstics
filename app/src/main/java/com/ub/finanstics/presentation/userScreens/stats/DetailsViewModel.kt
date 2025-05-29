package com.ub.finanstics.presentation.userScreens.stats

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.db.Action
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("TooGenericExceptionCaught")
class DetailsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Default)
    val uiState = _uiState.asStateFlow()

    var date = CalendarClass()

    private val _chosenCategory = MutableStateFlow(Pair("", -1))
    val chosenCategory: StateFlow<Pair<String, Int>> = _chosenCategory.asStateFlow()

    val db = FinansticsDatabase.getDatabase(application)
    val actionDao = db.actionDao()
    val categoryDao = db.categoryDao()

    fun getDetailedActions(
        category: String,
        type: Int
    ) {
        viewModelScope.launch {
            val cat = categoryDao.getCategoryByName(category)
            if (cat != null) {
                val actions = actionDao.getActionsDateByCategoryAndType(
                    date.getData().getMonth().number,
                    date.getData().getYear(),
                    cat.id,
                    type
                )
                    .sortedByDescending { it.value }
                _uiState.value = DetailsUiState.Detailed(
                    chosen = category,
                    type = type,
                    actions = actions
                )
            }
        }
    }

    fun hideDetailedActions() {
        _chosenCategory.value = Pair("", -1)
        _uiState.value = DetailsUiState.Default
    }

    fun viewAction(action: Action) {
        val uiState = _uiState.value
        if (uiState is DetailsUiState.Detailed) {
            _uiState.value = DetailsUiState.DetailedAction(
                actions = uiState.actions,
                chosen = uiState.chosen,
                action = action,
                type = uiState.type
            )
        }
    }

    fun hideAction() {
        val uiState = _uiState.value
        if (uiState is DetailsUiState.DetailedAction) {
            _uiState.value = DetailsUiState.Detailed(
                actions = uiState.actions,
                chosen = uiState.chosen,
                type = uiState.type
            )
        }
    }

    fun changeState(
        categoryName: String,
        type: Int
    ) {
        if (_uiState.value is DetailsUiState.Detailed) {
            val detailedUiState = _uiState.value as DetailsUiState.Detailed
            if (categoryName == detailedUiState.chosen && type == detailedUiState.type) {
                _chosenCategory.value = Pair("", -1)
                hideDetailedActions()
            } else {
                _chosenCategory.value = Pair(categoryName, type)
                getDetailedActions(categoryName, type)
            }
        } else {
            _chosenCategory.value = Pair(categoryName, type)
            getDetailedActions(categoryName, type)
        }
    }
}
