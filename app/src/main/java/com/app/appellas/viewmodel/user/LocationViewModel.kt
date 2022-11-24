package com.app.appellas.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.body.CreateLocationBody
import com.app.appellas.data.models.dtos.body.UpdateLocationUserBody
import com.app.appellas.data.models.dtos.response.CreateLocationResponse
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.repository.user.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationViewModel(private val repository: LocationRepository): ViewModel() {

    val userData = repository.findAllUsers()

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    private val _dismissDialog = MutableLiveData<Boolean?>()
    val dismissDialog: LiveData<Boolean?> = _dismissDialog

    //Funciones observables para mostrar los Dialogs Alerts
    fun startDismissDialog() {
        Log.d("ENTRO EN DISMISS", "ENTRO")
        _dismissDialog.value = true
    }

    fun endDismissDialog() {
        _dismissDialog.value = null
    }

    val location = MutableLiveData<CreateLocationResponse>()

    fun sendLocation(token: String, idUser: Int, latitud: Double, longitud: Double) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.dialog_alert_progressBar))

                val body = CreateLocationBody(
                    latitud,
                    longitud,
                    2,
                    idUser
                )

                val call = AppAPI.locationService.createLocation(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            location.value = data!!
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

    val titleAlert = MutableLiveData("")
    val descriptionAlert = MutableLiveData("")

    fun updateLocation(token: String, id: Long) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.dialog_description_alert_progressBar))

                val body = UpdateLocationUserBody(
                    id,
                    titleAlert.value.toString(),
                    descriptionAlert.value.toString()
                )

                val call = AppAPI.locationService.updateLocation(
                    "Bearer $token",
                    body
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            startDismissDialog()
                            _stateUI.value = UIState.Success
                            _stateUI.value = null
                        }else {
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