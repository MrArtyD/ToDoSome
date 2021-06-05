package com.example.todosome.todofragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.todosome.database.Task
import com.example.todosome.database.TasksDatabaseDao
import kotlinx.coroutines.*

class ToDoViewModel(private val databaseDao: TasksDatabaseDao) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    val tasks = databaseDao.getAllTasks()

    private val _clickedTask = MutableLiveData<Task?>()
    val clickedTask: LiveData<Task?>
        get() = _clickedTask

    val buttonsVisible = Transformations.map(tasks) {
        it.isNotEmpty()
    }

    init {
        _clickedTask.value = null
    }

    fun onTaskClicked(task: Task) {
        _clickedTask.value = task
    }

    fun onClear() {
        uiScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            databaseDao.clearAll()
        }
    }

    fun taskWasClicked(){
        _clickedTask.value = null
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}