package com.app.appellas.repository.user

import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.models.entities.User
import com.app.appellas.data.network.AppAPI

class LoginRepository(private val userDao: UserDao, private val appApi: AppAPI) {

    //Funciones del usuario
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun deleteUser() = userDao.deleteUsers()
    fun searchUser(user: Long) = userDao.searchUser(user)
    fun findAllUsers() = userDao.findAllUsers()

}