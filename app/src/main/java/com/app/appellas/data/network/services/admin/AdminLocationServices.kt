package com.app.appellas.data.network.services.admin

import com.app.appellas.data.models.dtos.body.UpdateLocationBody
import com.app.appellas.data.models.dtos.response.GenericResponse
import com.app.appellas.data.models.dtos.response.LocationResponse
import retrofit2.Response
import retrofit2.http.*

interface AdminLocationServices {

    @GET("location")
    suspend fun getAllLocations(@Header("Authorization") token: String): Response<GenericResponse<List<LocationResponse>>>

    @PUT("location")
    suspend fun updateLocation(@Header("Authorization") token: String, @Body requestBody: List<UpdateLocationBody>): Response<GenericResponse<String>>

    @GET("location/detail/{id}")
    suspend fun getLocationDetail(@Header("Authorization") token: String, @Path("id") id: String): Response<GenericResponse<LocationResponse>>

    @GET("location/detail/maps_id/{id}")
    suspend fun getAlertDetailMapsId(@Header("Authorization") token: String, @Path("id") id: String): Response<GenericResponse<LocationResponse>>

}