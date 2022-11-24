package com.app.appellas.viewmodel.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.UserResponse
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.repository.admin.AdminUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUserViewModel(private val repository: AdminUserRepository): ViewModel() {

    val userData = repository.findAllUsers()

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    val usersList = MutableLiveData<List<UserResponse>>()

    private val _stateUI2 = MutableLiveData<Boolean?>()
    val stateUI2: LiveData<Boolean?> = _stateUI2

    fun changeState() {
        _stateUI2.value = true
    }

    fun endChangeState() {
        _stateUI2.value = null
    }

    fun getAllUsers(token: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.admin_manage_acc_progressBar))

                val call = AppAPI.adminUserServices.getUsers(
                    "Bearer $token"
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            usersList.value = data!!
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
                        }
                    } else {
                        Log.d("Error", "Error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", "Error")
        }
    }

    fun deleteUser(token: String, id: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.admin_manage_acc_progressBar))

                val call = AppAPI.adminUserServices.deleteUser(
                    "Bearer $token",
                    id
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            changeState()
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
                        }
                    } else {
                        Log.d("Error", "Error")
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { Log.d("Error", it) }
        }
    }

}