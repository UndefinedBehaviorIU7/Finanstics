package com.example.finanstics.presentation.group.stats

import android.app.Application
import retrofit2.HttpException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<GroupStatsUiState>(GroupStatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val repository = GroupStatsRepository()

    fun fetchData() {
        _uiState.value = GroupStatsUiState.Loading
        viewModelScope.launch {
            try {
                val incomes = repository.getIncomes()
                val expenses = repository.getExpenses()
                val calendar = java.util.Calendar.getInstance()
                val month = calendar.get(java.util.Calendar.MONTH) + 1
                _uiState.value = GroupStatsUiState.Done(
                    incomes = incomes,
                    expenses = expenses,
                    month = month)
            } catch (e: HttpException) {
                _uiState.value = GroupStatsUiState.Error(" ${e.localizedMessage}")
            }
        }
    }
}
