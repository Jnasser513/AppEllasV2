package com.app.appellas.repository.admin

import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.network.AppAPI

class AdminUserRepository(private val userDao: UserDao, private val appApi: AppAPI) {

    fun findAllUsers() = userDao.findAllUsers()

}