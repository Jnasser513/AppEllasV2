package com.app.appellas.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.body.CreateContactBody
import com.app.appellas.data.models.dtos.response.ContactResponse
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.repository.user.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(private val repository: ContactRepository): ViewModel() {

    val userData = repository.findAllUsers()
    val contactList = MutableLiveData<List<ContactResponse>>()

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    val name = MutableLiveData("")
    val lastName = MutableLiveData("")
    val email = MutableLiveData("")
    val number = MutableLiveData("")

    private val _stateUI2 = MutableLiveData<Boolean?>()
    val stateUI2: LiveData<Boolean?> = _stateUI2

    fun changeState() {
        _stateUI2.value = true
    }

    fun endChangeState() {
        _stateUI2.value = null
    }

    fun createContact(token: String, idUser: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.create_contact_progressBar))

                val completeName =  "${name.value.toString()} ${lastName.value.toString()}"

                val body = CreateContactBody(
                    completeName,
                    number.value.toString().toLong(),
                    idUser,
                    email.value.toString().trim()
                )

                val call = AppAPI.contactService.createContact(
                    "Bearer $token",
                    body
                )
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

    fun getAllContacts(token: String, idUser: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.contact_progressBar))

                val call = AppAPI.contactService.getUserContacts(
                    "Bearer $token",
                    idUser
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            contactList.value = data!!
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

    fun deleteContact(token: String, idUser: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.dialog_delete_progressBar))

                val call = AppAPI.contactService.deleteContact(
                    "Bearer $token",
                    idUser
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
            Log.d("Error", "Error")
        }
    }

}