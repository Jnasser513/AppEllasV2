package com.app.appellas.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.appellas.R
import com.app.appellas.data.models.dtos.response.BlogsResponse
import com.app.appellas.data.models.dtos.body.CreateBlogBody
import com.app.appellas.data.network.AppAPI
import com.app.appellas.data.network.UIState
import com.app.appellas.repository.user.BlogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlogViewModel(private val repository: BlogRepository): ViewModel() {

    val userData = repository.findAllUsers()

    private val _stateUI = MutableLiveData<UIState<Int>?>()
    val stateUI: LiveData<UIState<Int>?> = _stateUI

    val title = MutableLiveData("")
    val subtitle = MutableLiveData("")
    val description = MutableLiveData("")

    //Variables para guardar y mostrar datos
    val blogsList = MutableLiveData<List<BlogsResponse>?>()
    val voucher = MutableLiveData("")

    fun createBlog(token: String, id: Int) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.create_blog_progressBar))

                val image = voucher.value.toString().replace("\n", "")

                val body = CreateBlogBody(
                    title.value.toString(),
                    subtitle.value.toString(),
                    description.value.toString(),
                    id,
                    "data:image/png;base64,$image"
                )

                val call = AppAPI.blogService.createBlog(
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
                        } else {
                            _stateUI.value = UIState.Error(message)
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

    fun getAllBlogs(token: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _stateUI.postValue(UIState.Loading(R.id.blogs_progressBar))

                val call = AppAPI.blogService.getAllBlogs(
                    "Bearer $token"
                )
                val response = call.body()
                val errorCode = response?.code ?: 0
                val data = response?.data
                val message = response?.message ?: "Error"

                withContext(Dispatchers.Main) {
                    if(call.isSuccessful) {
                        if(errorCode == 1) {
                            blogsList.value = data
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

    fun getVoucherImage(image: String): String {
        return voucher.postValue(image).toString()
    }

}