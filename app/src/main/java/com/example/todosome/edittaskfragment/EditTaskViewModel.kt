package com.example.todosome.edittaskfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todosome.database.Task
import com.example.todosome.database.TasksDatabaseDao
import kotlinx.coroutines.*

class EditTaskViewModel(val databaseDao: TasksDatabaseDao) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _returnBack = MutableLiveData<Boolean?>()
    val returnBack: LiveData<Boolean?>
        get() = _returnBack

    private val _isEditTextFilled = MutableLiveData<Boolean?>()
    val isEditTextFilled: LiveData<Boolean?>
        get() = _isEditTextFilled

    init {
        _returnBack.value = null
        _isEditTextFilled.value = null
    }

    fun updateTask(taskId: Long, newTitle: String, newDescription: String) {
        if (newTitle.isEmpty()){
            _isEditTextFilled.value = false
        }else{
            _isEditTextFilled.value = true

            uiScope.launch {
                val task = getTask(taskId)
                task.title = newTitle
                task.description = newDescription
                update(task)
            }
        }

    }

    private suspend fun getTask(taskId: Long): Task {
        var task: Task
        withContext(Dispatchers.IO) {
            task = databaseDao.get(taskId)
        }
        return task
    }

    private suspend fun update(task: Task) {
        withContext(Dispatchers.IO) {
            databaseDao.update(task)
        }
        _returnBack.value = true
    }

    fun deleteTask(taskId: Long) {
        uiScope.launch {
            val task = getTask(taskId)
            delete(task)
        }
    }

    private suspend fun delete(task: Task) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(task)
        }
        _returnBack.value = true
    }


    fun doneEditing() {
        _returnBack.value = null
    }

//    fun editTextFilled() {
//        _isEditTextFilled.value = true
//    }
}