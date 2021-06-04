package com.example.todosome.todofragment

import androidx.lifecycle.ViewModel
import com.example.todosome.database.TasksDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ToDoViewModel(val databaseDao: TasksDatabaseDao) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    val tasks = databaseDao.getAllTasks()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}