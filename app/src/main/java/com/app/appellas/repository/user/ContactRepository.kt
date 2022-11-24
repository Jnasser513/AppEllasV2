package com.app.appellas.repository.user

import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.network.AppAPI
import okhttp3.internal.userAgent

class ContactRepository(private val userDao: UserDao,private val api: AppAPI) {

    fun findAllUsers() = userDao.findAllUsers()

}