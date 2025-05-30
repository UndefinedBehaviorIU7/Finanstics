package com.ub.finanstics.db

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
    suspend fun getActionsByDate(day: Int, month: Int, year: Int): List<Action>

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

    @Query("UPDATE actions SET createdAt = :creationTime WHERE actionId = :actionId")
    suspend fun updateCreationTime(actionId: Int, creationTime: String)

    @Query("SELECT * FROM actions WHERE serverId = :serverId LIMIT 1")
    suspend fun getActionByServerId(serverId: Int): Action?

    @Query(
        "SELECT * FROM actions WHERE strftime('%m', date) = printf('%02d', :month) " +
            "AND strftime('%Y', date) = printf('%d', :year) " +
            "AND categoryId = :categoryId AND type = :type"
    )

    suspend fun getActionsDateByCategoryAndType(
        month: Int,
        year: Int,
        categoryId: Int,
        type: Int
    ): List<Action>
}
