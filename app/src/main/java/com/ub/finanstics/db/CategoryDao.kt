package com.ub.finanstics.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Suppress("TooManyFunctions")
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

    @Query("UPDATE categories SET createdAt = :creationTime WHERE id = :categoryId")
    suspend fun updateCreationTime(categoryId: Int, creationTime: String)

    @Query("SELECT * FROM categories WHERE serverId = :serverId")
    suspend fun getCategoryByServerId(serverId: Int): Category?
}
