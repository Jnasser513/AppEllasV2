package com.app.appellas.data.network.services.user

import com.app.appellas.data.models.dtos.response.BlogsResponse
import com.app.appellas.data.models.dtos.body.CreateBlogBody
import com.app.appellas.data.models.dtos.response.CreateBlogResponse
import com.app.appellas.data.models.dtos.response.GenericResponse
import retrofit2.Response
import retrofit2.http.*

interface BlogService {

    @POST("blogs")
    suspend fun createBlog(@Header("Authorization") token: String, @Body requestBody: CreateBlogBody): Response<GenericResponse<CreateBlogResponse>>

    @GET("blogs")
    suspend fun getAllBlogs(@Header("Authorization") token: String): Response<GenericResponse<List<BlogsResponse>>>

    @GET("blogs/detail/{id}")
    suspend fun getBlogDetail(@Header("Authorization") token: String, @Path("id") id: Int): Response<GenericResponse<BlogsResponse>>
}