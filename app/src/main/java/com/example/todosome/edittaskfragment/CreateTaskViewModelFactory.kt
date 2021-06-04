package com.example.todosome.edittaskfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todosome.database.TasksDatabaseDao
import java.lang.IllegalArgumentException

class CreateTaskViewModelFactory(private val databaseDao: TasksDatabaseDao): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)){
            return CreateTaskViewModel(databaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}