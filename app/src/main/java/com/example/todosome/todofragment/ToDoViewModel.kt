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

    private var _currentTasks = MutableLiveData<List<Task>>()
    val currentTasks : LiveData<List<Task>>
        get() = _currentTasks

    private val _clickedTask = MutableLiveData<Task?>()
    val clickedTask: LiveData<Task?>
        get() = _clickedTask

    private val _isTaskFinished = MutableLiveData<Boolean?>()
    val isTaskFinished: LiveData<Boolean?>
        get() = _isTaskFinished

    val buttonsVisible = Transformations.map(currentTasks) {
        it?.isNotEmpty()
    }

    init {
        _clickedTask.value = null
        _isTaskFinished.value = null
        filterState.value = FilterStatus.ACTIVE_TASKS
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

    fun showAllTasks() {
        if (filterState.value != FilterStatus.ALL_TASKS){
            filterState.value = FilterStatus.ALL_TASKS
            checkFilterStatus(allTasks.value!!)
        }
    }


    fun showActiveTasks(){
        if (filterState.value != FilterStatus.ACTIVE_TASKS){
            filterState.value = FilterStatus.ACTIVE_TASKS
            checkFilterStatus(allTasks.value!!)
        }
    }

    fun showCompletedTasks(){
        if (filterState.value != FilterStatus.COMPLETED_TASKS){
            filterState.value = FilterStatus.COMPLETED_TASKS
            checkFilterStatus(allTasks.value!!)
        }
    }

    fun checkFilterStatus(list: List<Task>) {
        _currentTasks.value = when(filterState.value){
            FilterStatus.ACTIVE_TASKS -> list.filter { !it.isCompleted }
            FilterStatus.COMPLETED_TASKS -> list.filter { it.isCompleted }
            else -> list
        }
    }

}