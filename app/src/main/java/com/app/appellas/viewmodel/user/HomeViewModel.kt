package com.app.appellas.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.LocationResponse
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class HomeViewModel(): ViewModel() {

    private val _goAlertDetail = MutableLiveData<Boolean?>()
    val goAlertDetail: LiveData<Boolean?> = _goAlertDetail

    var notificationId = MutableLiveData("")

    fun goAlertDetail(id: String) {
        _goAlertDetail.value = true
        notificationId.value = id
    }

    fun endGoAlertDetail() {
        _goAlertDetail.value = null
    }

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    val alertDetail = MutableLiveData<LocationResponse>()

    fun getAlertDetail(token: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val call = AppAPI.locationService.getAlertDetail("Bearer $token", id)
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            alertDetail.value = data!!
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        } else {
                            _stateUI.value = UIState.Error(message)
                            _stateUI.value = null
                        }
                    } else {
                        _stateUI.value = UIState.Error("Error de coneccion")
                    }
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    _stateUI.value = UIState.Error("Error de coneccion")
                }
            }
        }
    }

}