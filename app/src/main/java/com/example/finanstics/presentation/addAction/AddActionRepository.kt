package com.example.finanstics.presentation.addAction

import com.example.finanstics.db.FinansticsDatabase

class AddActionRepository(private var db: FinansticsDatabase) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(): List<String> {
        val categories = categoryDao.getAllCategories()
        return categories.map { it.name }
    }
}
