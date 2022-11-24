package com.app.appellas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.appellas.repository.admin.AdminAdvisorRepository
import com.app.appellas.repository.admin.AdminLocationRepository
import com.app.appellas.repository.admin.AdminUserRepository
import com.app.appellas.repository.user.BlogRepository
import com.app.appellas.repository.user.ContactRepository
import com.app.appellas.repository.user.LocationRepository
import com.app.appellas.repository.user.LoginRepository
import com.app.appellas.viewmodel.admin.AdminAdvisorViewModel
import com.app.appellas.viewmodel.admin.AdminLocationViewModel
import com.app.appellas.viewmodel.admin.AdminUserViewModel
import com.app.appellas.viewmodel.user.AuthViewModel
import com.app.appellas.viewmodel.user.BlogViewModel
import com.app.appellas.viewmodel.user.ContactViewModel
import com.app.appellas.viewmodel.user.LocationViewModel
import java.lang.Exception

class ViewModelFactory<R>(private val repository: R): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository as LoginRepository) as T
        }
        if(modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            return BlogViewModel(repository as BlogRepository) as T
        }
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(repository as LocationRepository) as T
        }
        if(modelClass.isAssignableFrom(AdminUserViewModel::class.java)) {
            return AdminUserViewModel(repository as AdminUserRepository) as T
        }
        if(modelClass.isAssignableFrom(AdminAdvisorViewModel::class.java)) {
            return AdminAdvisorViewModel(repository as AdminAdvisorRepository) as T
        }
        if(modelClass.isAssignableFrom(AdminLocationViewModel::class.java)) {
            return AdminLocationViewModel(repository as AdminLocationRepository) as T
        }
        if(modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(repository as ContactRepository) as T
        }
        throw Exception("Unknown viewmodel class")
    }
}