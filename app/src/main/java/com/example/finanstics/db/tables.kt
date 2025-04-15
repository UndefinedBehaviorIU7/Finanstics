package com.example.finanstics.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.finanstics.presentation.calendar.MonthNameClass

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: Int,
    val serverId: Int? = null
)

@Entity(
    tableName = "actions",
    foreignKeys =
        [
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
    val day: Int,
    val month: MonthNameClass,
    val year: Int,
    @ColumnInfo(index = true) val categoryId: Int,
    val description: String? = null,
    val createdAt: String? = null,
    val serverId: Int? = null
)
