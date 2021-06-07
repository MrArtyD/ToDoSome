package com.example.todosome.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TasksDatabaseDao {

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("SELECT * FROM task_to_do WHERE taskId = :id")
    fun get(id: Long): Task

    @Query("SELECT * FROM task_to_do ORDER BY taskId DESC")
    fun getAllTasks(): LiveData<List<Task>>

//    @Query("SELECT * FROM task_to_do WHERE is_completed = 0")
//    fun getToDoTasks(): LiveData<List<Task>>
//
//    @Query("SELECT * FROM task_to_do WHERE is_completed = 1")
//    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("DELETE FROM task_to_do")
    fun clearAll()
}