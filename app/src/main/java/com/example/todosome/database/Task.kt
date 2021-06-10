package com.example.todosome.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_to_do")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "creation_time_millis")
    val creationTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "completion_time_millis")
    var completionTime: Long = 0L,

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false
)