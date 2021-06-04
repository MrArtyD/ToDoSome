package com.example.todosome.createtaskfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todosome.database.Task
import com.example.todosome.database.TasksDatabaseDao
import kotlinx.coroutines.*

class CreateTaskViewModel(private val databaseDao: TasksDatabaseDao) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _isEditTextFilled = MutableLiveData<Boolean?>()
    val isEditTextFilled: LiveData<Boolean?>
        get() = _isEditTextFilled

    private val _taskCreated = MutableLiveData<Boolean?>()
    val taskCreated: LiveData<Boolean?>
        get() = _taskCreated

    init {
        _isEditTextFilled.value = null
        _taskCreated.value = null
    }

    fun createTask(title: String, description: String) {
        if (title.isEmpty()) {
            _isEditTextFilled.value = false
        } else {
            uiScope.launch {
                val task = Task(title = title, description = description)
                insert(task)
            }
        }
    }

    private suspend fun insert(task: Task) {
        withContext(Dispatchers.IO) {
            databaseDao.insert(task)
        }
        _taskCreated.value = true
    }

    fun editTextFilled() {
        _isEditTextFilled.value = true
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun doneWithCreation() {
        _isEditTextFilled.value = null
        _taskCreated.value = null
    }
}