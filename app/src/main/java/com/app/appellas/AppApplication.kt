package com.app.appellas

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.app.appellas.data.AppEllasDB
import com.app.appellas.data.network.AppAPI
import com.app.appellas.repository.admin.AdminAdvisorRepository
import com.app.appellas.repository.admin.AdminLocationRepository
import com.app.appellas.repository.admin.AdminUserRepository
import com.app.appellas.repository.user.BlogRepository
import com.app.appellas.repository.user.ContactRepository
import com.app.appellas.repository.user.LocationRepository
import com.app.appellas.repository.user.LoginRepository
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val database by lazy {
        AppEllasDB.getDatabase(this)
    }

    val loginRepository by lazy {
        val userDao = database.userDao()
        LoginRepository(userDao, AppAPI)
    }

    val blogRepository by lazy {
        val userDao = database.userDao()
        BlogRepository(userDao, AppAPI)
    }

    val locationRepository by lazy {
        val userDao = database.userDao()
        LocationRepository(userDao, AppAPI)
    }

    val adminUserRepository by lazy {
        val userDao = database.userDao()
        AdminUserRepository(userDao, AppAPI)
    }

    val adminAdvisorRepository by lazy {
        val userDao = database.userDao()
        AdminAdvisorRepository(userDao, AppAPI)
    }

    val adminLocationRepository by lazy {
        val userDao = database.userDao()
        AdminLocationRepository(userDao, AppAPI)
    }

    val contactRepository by lazy {
        val userDao = database.userDao()
        ContactRepository(userDao, AppAPI)
    }
}