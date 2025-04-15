package com.example.finanstics.presentation.addAction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.stats.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddActionViewModel(
    application: Application
) : AndroidViewModel(application) {

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = AddActionRepository(db)

    private val _uiState = MutableStateFlow<AddActionUiState>(
        AddActionUiState.Idle(
            typeAction = "",
            nameAction = "",
            moneyAction = 0,
            data = "",
            category = "",
            description = "",
            allCategory = listOf(),
            menuExpandedType = false,
            menuExpandedCategory = false,
        )
    )

    val uiState = _uiState.asStateFlow()

    fun getCategoriesList() {
        viewModelScope.launch {
            val categoryNames = repository.getCategoriesNames()
            _uiState.update { currentState ->
                when (currentState) {
                    is AddActionUiState.Idle -> currentState.copy(allCategory = categoryNames)
                    else -> currentState
                }
            }
        }
    }

    init {
        getCategoriesList()
    }

    @Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
    fun updateUIState(
        newTypeAction: String? = null,
        newNameAction: String? = null,
        newMoneyAction: Int? = null,
        newData: String? = null,
        newCategory: String? = null,
        newDescription: String? = null,
        newMenuExpandedType: Boolean? = null,
        newMenuExpandedCategory: Boolean? = null,
    ) {
        when (val current = _uiState.value) {
            is AddActionUiState.Idle -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
                )
            }

            is AddActionUiState.Error -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
                )
            }

            is AddActionUiState.Loading -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
                )
            }
        }
    }
}
