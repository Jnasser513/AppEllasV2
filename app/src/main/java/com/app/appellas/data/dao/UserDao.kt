package com.app.appellas.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.appellas.data.models.entities.User

@Dao
interface UserDao {

    //Insertar usuario en la base de datos local
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    //Borrar usuario
    @Query("DELETE FROM user_table")
    fun deleteUsers()

    //Buscar usuario
    @Query("SELECT * FROM user_table WHERE id = :id")
    fun searchUser(id: Long): User

    @Query("SELECT * FROM user_table")
    fun findAllUsers(): LiveData<List<User>>
}