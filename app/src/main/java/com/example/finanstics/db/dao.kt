package com.example.finanstics.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.finanstics.presentation.calendar.MonthNameClass

@Suppress("TooManyFunctions")
@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: Action)

    @Update
    suspend fun updateAction(action: Action)

    @Delete
    suspend fun deleteAction(action: Action)

    @Query("SELECT * FROM actions WHERE actionId = :id")
    suspend fun getActionById(id: Int): Action?

    @Query("SELECT * FROM actions")
    suspend fun getAllActions(): List<Action>

    @Query("SELECT * FROM actions WHERE categoryId = :categoryId")
    suspend fun getActionsByCategory(categoryId: Int): List<Action>

    @Query("""
    SELECT * FROM actions 
    WHERE strftime('%m', date) = printf('%02d', :month) 
    AND strftime('%Y', date) = printf('%d', :year)
    AND strftime('%d', date) = printf('%02d', :day)
""")
    suspend fun getActionsByDate(month: Int, year: Int, day: Int): List<Action>

    @Query("SELECT * FROM actions WHERE type = 0")
    suspend fun getAllIncomes(): List<Action>

    @Query("SELECT * FROM actions WHERE type = 1")
    suspend fun getAllExpenses(): List<Action>

    @Query("""
    SELECT * FROM actions 
    WHERE strftime('%m', date) = printf('%02d', :month) 
    AND strftime('%Y', date) = printf('%d', :year)
    AND type = 1
""")
    suspend fun getIncomesByMonthYear(month: Int, year: Int): List<Action>

    @Query("""
    SELECT * FROM actions 
    WHERE strftime('%m', date) = printf('%02d', :month) 
    AND strftime('%Y', date) = printf('%d', :year)
    AND type = 0
""")
    suspend fun getExpensesByMonthYear(month: Int, year: Int): List<Action>
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): Category?

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE type = 2 OR type = 1")
    suspend fun getIncomesCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE type = 0 OR type = 1")
    suspend fun getExpensesCategories(): List<Category>
}
