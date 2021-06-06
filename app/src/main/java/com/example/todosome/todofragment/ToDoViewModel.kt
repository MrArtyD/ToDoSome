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

    val tasks = databaseDao.getToDoTasks()

    private val _clickedTask = MutableLiveData<Task?>()
    val clickedTask: LiveData<Task?>
        get() = _clickedTask

    private val _isTaskFinished = MutableLiveData<Boolean?>()
    val isTaskFinished: LiveData<Boolean?>
        get() = _isTaskFinished

    val buttonsVisible = Transformations.map(tasks) {
        it.isNotEmpty()
    }

    init {
        _clickedTask.value = null
        _isTaskFinished.value = null
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

    fun onTaskCompleted(task: Task) {
        if (task.isCompleted){
            uiScope.launch {
                update(task)
            }
        }
    }

    private suspend fun update(task: Task){
        withContext(Dispatchers.IO){
            databaseDao.update(task)
        }
        _isTaskFinished.value = true
    }

    fun taskCompleted(){
        _isTaskFinished.value = null
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}