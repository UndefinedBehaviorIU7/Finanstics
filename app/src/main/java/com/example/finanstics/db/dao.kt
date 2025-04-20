package com.example.finanstics.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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

    @Query(
        "SELECT * FROM actions WHERE strftime('%m', date) = printf('%02d', :month) " +
            "AND strftime('%Y', date) = printf('%d', :year) " +
            "AND strftime('%d', date) = printf('%02d', :day)"
    )
    suspend fun getActionsByDate(month: Int, year: Int, day: Int): List<Action>

    @Query("SELECT * FROM actions WHERE type = 0")
    suspend fun getAllIncomes(): List<Action>

    @Query("SELECT * FROM actions WHERE type = 1")
    suspend fun getAllExpenses(): List<Action>

    @Query(
        "SELECT * FROM actions " +
            "WHERE strftime('%m', date) = printf('%02d', :month) " +
            "AND strftime('%Y', date) = printf('%d', :year) " +
            "AND type = 1"
    )
    suspend fun getIncomesByMonthYear(month: Int, year: Int): List<Action>

    @Query(
        "SELECT * FROM actions WHERE strftime('%m', date) = printf('%02d', :month) " +
            "AND strftime('%Y', date) = printf('%d', :year) " +
            "AND type = 0"
    )
    suspend fun getExpensesByMonthYear(month: Int, year: Int): List<Action>

    @Query("SELECT * FROM actions WHERE serverId IS NULL")
    suspend fun getUnsyncedActions(): List<Action>

    @Query("UPDATE actions SET serverId = :serverId WHERE actionId = :actionId")
    suspend fun updateServerId(actionId: Int, serverId: Int)

    @Query("SELECT * FROM actions WHERE serverId = :serverId LIMIT 1")
    suspend fun getActionByServerId(serverId: Int): Action?
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

    @Query("SELECT * FROM categories WHERE name = :name AND type = :type")
    suspend fun getCategoryByNameAndType(name: String, type: Int): Category?

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE type = 2 OR type = 1")
    suspend fun getIncomesCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE type = 0 OR type = 1")
    suspend fun getExpensesCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE serverId IS NULL")
    suspend fun getUnsyncedCategories(): List<Category>

    @Query("UPDATE categories SET serverId = :serverId WHERE id = :categoryId")
    suspend fun updateServerId(categoryId: Int, serverId: Int)

    @Query("SELECT * FROM categories WHERE serverId = :serverId")
    suspend fun getCategoryByServerId(serverId: Int): Category?
}
