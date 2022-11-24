package com.app.appellas.data.models.dtos.response

import com.google.gson.annotations.SerializedName

class GenericResponse<T>(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("errorCode") var code: Int = 0,
    @SerializedName("data") var data: T? = null,
    @SerializedName("message") var message: String? = null
)