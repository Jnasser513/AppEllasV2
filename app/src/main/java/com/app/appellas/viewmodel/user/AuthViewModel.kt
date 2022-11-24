package com.app.appellas.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.body.*
import com.app.appellas.data.models.dtos.response.LoginResponse
import com.app.appellas.data.models.entities.User
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.repository.user.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AuthViewModel(private val repository: LoginRepository): ViewModel() {

    val userData = repository.findAllUsers()

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    private val _goHomeFragment = MutableLiveData<Boolean?>()
    val goHomeFragment: LiveData<Boolean?> = _goHomeFragment

    private fun goHomeFragment() {
        _goHomeFragment.value = true
    }

    fun endGoHomeFragment() {
        _goHomeFragment.value = null
    }

    private val _goAdminFragment = MutableLiveData<Boolean?>()
    val goAdminFragment: LiveData<Boolean?> = _goAdminFragment

    private fun goAdminFragment() {
        _goAdminFragment.value = true
    }

    fun endGoAdminFragment() {
        _goAdminFragment.value = null
    }

    val userInfo = MutableLiveData<LoginResponse>()

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    fun login(token: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.login_progressBar))

                val request = LoginRequest(
                    email.value.toString().trim().lowercase(Locale.getDefault()),
                    password.value.toString(),
                    token
                )

                val call = AppAPI.loginService.login(request)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data?.get(0)
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            userInfo.value = data!!
                            Log.d("Success", "Success")
                            withContext(Dispatchers.IO) {
                                val user = User(
                                    data.id,
                                    data.username,
                                    data.nombre,
                                    data.email,
                                    data.rol,
                                    data.accessToken
                                )

                                repository.insertUser(user)
                            }
                            when {
                                data?.rol?.equals("usuario") == true -> {
                                    goHomeFragment()
                                }
                                data?.rol?.equals("admin") == true -> {
                                    goAdminFragment()
                                }
                            }
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            Log.d("Error", "Error")
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

    val nameRegister = MutableLiveData("")
    val phoneRegister = MutableLiveData("")
    val emailRegister = MutableLiveData("")
    val passwordRegister = MutableLiveData("")

    fun register() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.create_account_progressBar))

                val body = RegisterBody(
                    nameRegister.value.toString(),
                    phoneRegister.value!!.toInt(),
                    emailRegister.value.toString().trim().lowercase(Locale.getDefault()),
                    passwordRegister.value.toString(),
                    1
                )

                val call = AppAPI.loginService.register(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                            //goLoginFragment()
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value =  null
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

    val emailRecovery = MutableLiveData("")

    fun sendRecoveryCode() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.enter_email_progressBar))

                val body = SendCodeBody(
                    emailRecovery.value.toString()
                )

                val call = AppAPI.loginService.sendRecoveryCode(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
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

    val recoveryCode1 = MutableLiveData("")
    val recoveryCode2 = MutableLiveData("")
    val recoveryCode3 = MutableLiveData("")
    val recoveryCode4 = MutableLiveData("")
    val recoveryCode5 = MutableLiveData("")

    fun verifyCode(email: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.recovery_code_progressBar))

                val completeCode = recoveryCode1.value.toString() + recoveryCode2.value.toString() +
                        recoveryCode3.value.toString() + recoveryCode4.value.toString() + recoveryCode5.value.toString()

                val body = VerifyCodeBody(
                    email.trim().lowercase(),
                    completeCode.toString()
                )

                val call = AppAPI.loginService.verifyCode(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
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

    val passwordChange = MutableLiveData("")

    fun changePassword(code: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.change_password_progressBar))

                val body = ChangePasswordBody(
                    code,
                    passwordChange.value.toString()
                )

                val call = AppAPI.loginService.changePassword(body)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
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

    fun signOut() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteUser()
            }
        } catch (e: Exception) {
            Log.d("Error", "Error in close session")
        }
    }

}