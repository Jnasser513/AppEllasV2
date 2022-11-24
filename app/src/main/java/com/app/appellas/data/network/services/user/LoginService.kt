package com.app.appellas.data.network.services.user

import com.app.appellas.data.models.dtos.body.*
import com.app.appellas.data.models.dtos.response.GenericResponse
import com.app.appellas.data.models.dtos.response.LoginResponse
import com.app.appellas.data.models.dtos.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("auth/login")
    suspend fun login(@Body requestBody: LoginRequest): Response<GenericResponse<List<LoginResponse>>>

    @POST("auth/signup")
    suspend fun register(@Body requestBody: RegisterBody): Response<GenericResponse<RegisterResponse>>

    @POST("auth/send_code")
    suspend fun sendRecoveryCode(@Body requestBody: SendCodeBody): Response<GenericResponse<String>>

    @POST("auth/verify_code")
    suspend fun verifyCode(@Body requestBody: VerifyCodeBody): Response<GenericResponse<String>>

    @POST("auth/change_password")
    suspend fun changePassword(@Body requestBody: ChangePasswordBody): Response<GenericResponse<String>>

}