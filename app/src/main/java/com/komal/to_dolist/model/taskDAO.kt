package com.komal.to_dolist.model

import androidx.room.*

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity) //INSERT NEW TASK TO DATABASE

    @Update
    suspend fun updateTask(task: TaskEntity) //UPDATE EXISTING TASK

    @Delete
    suspend fun deleteTask(task: TaskEntity) //DELETE TASK ENTITY

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    suspend fun getAllTasks(): List<TaskEntity>  //GET TASKS BY ID
}