package com.app.appellas.repository.admin

import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.network.AppAPI

class AdminAdvisorRepository(private val userDao: UserDao, private val api: AppAPI) {

    fun findAllUsers() = userDao.findAllUsers()

}