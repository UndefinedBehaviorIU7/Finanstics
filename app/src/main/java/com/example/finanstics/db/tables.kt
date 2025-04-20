package com.example.finanstics.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: Int,
    var serverId: Int? = null
)

@Entity(
    tableName = "actions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)

data class Action(
    @PrimaryKey(autoGenerate = true) val actionId: Int = 0,
    val type: Int,
    val name: String,
    val value: Int,
    val date: LocalDate,
    @ColumnInfo(index = true) val categoryId: Int,
    val description: String? = null,
    val createdAt: String? = null,
    var serverId: Int? = null
)
