package com.app.appellas.repository.user

import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.network.AppAPI

class LocationRepository(private val userDao: UserDao, private val appApi: AppAPI) {

    fun findAllUsers() = userDao.findAllUsers()

}