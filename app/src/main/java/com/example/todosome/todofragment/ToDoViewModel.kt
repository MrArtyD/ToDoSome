package com.example.todosome.todofragment

import androidx.lifecycle.*
import com.example.todosome.database.Task
import com.example.todosome.database.TasksDatabaseDao
import kotlinx.coroutines.*

class ToDoViewModel(private val databaseDao: TasksDatabaseDao) : ViewModel() {

    enum class FilterStatus{
        ALL_TASKS,
        ACTIVE_TASKS,
        COMPLETED_TASKS
    }

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var filterState = MutableLiveData<FilterStatus>()

    private var _allTasks = databaseDao.getAllTasks()
    val allTasks : LiveData<List<Task>>
        get() = _allTasks

    private var _filteredTasks = MutableLiveData<List<Task>>()
    val filteredTasks : LiveData<List<Task>>
        get() = _filteredTasks

    private val _clickedTask = MutableLiveData<Task?>()
    val clickedTask: LiveData<Task?>
        get() = _clickedTask

    private val _isTaskFinished = MutableLiveData<Boolean?>()
    val isTaskFinished: LiveData<Boolean?>
        get() = _isTaskFinished


    init {
        _clickedTask.value = null
        _isTaskFinished.value = null
        filterState.value = FilterStatus.ACTIVE_TASKS
    }

    fun onTaskClicked(task: Task) {
        _clickedTask.value = task
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

    fun showAllTasks() {
        if (filterState.value != FilterStatus.ALL_TASKS){
            filterState.value = FilterStatus.ALL_TASKS
            filterTasks(allTasks.value!!)
        }
    }

    fun showActiveTasks(){
        if (filterState.value != FilterStatus.ACTIVE_TASKS){
            filterState.value = FilterStatus.ACTIVE_TASKS
            filterTasks(allTasks.value!!)
        }
    }

    fun showCompletedTasks(){
        if (filterState.value != FilterStatus.COMPLETED_TASKS){
            filterState.value = FilterStatus.COMPLETED_TASKS
            filterTasks(allTasks.value!!)
        }
    }

    fun filterTasks(list: List<Task>) {
        _filteredTasks.value = when(filterState.value){
            FilterStatus.ACTIVE_TASKS -> list.filter { !it.isCompleted }
            FilterStatus.COMPLETED_TASKS -> list.filter { it.isCompleted }
            else -> list
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}