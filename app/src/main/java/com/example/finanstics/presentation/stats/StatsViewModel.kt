package com.example.finanstics.presentation.stats

import android.app.Application
import retrofit2.HttpException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val repository = StatsRepository()

    fun fetchData() {
        _uiState.value = StatsUiState.Loading
        viewModelScope.launch {
            try {
                val incomes = repository.getIncomes()
                val expenses = repository.getExpenses()
                _uiState.value = StatsUiState.Done(incomes = incomes, expenses = expenses)
            } catch (e: HttpException) {
                _uiState.value = StatsUiState.Error(" ${e.localizedMessage}")
            }
        }
    }
}
