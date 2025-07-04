package com.komal.to_dolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.komal.to_dolist.model.DatabaseProvider
import com.komal.to_dolist.model.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = DatabaseProvider.getDatabase(application).taskDao()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> get() = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = taskDao.getAllTasks()
        }
    }

    fun addTask(name: String, duration: Int) {
        viewModelScope.launch {
            val newTask = TaskEntity(name = name, tduration = duration)
            taskDao.insertTask(newTask)
            loadTasks()
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
            loadTasks()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            loadTasks()
        }
    }
}