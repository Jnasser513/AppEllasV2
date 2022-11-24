package com.app.appellas.data.network

import com.app.appellas.data.network.services.WidgetService
import com.app.appellas.data.network.services.admin.AdminAdvisoryService
import com.app.appellas.data.network.services.admin.AdminLocationServices
import com.app.appellas.data.network.services.admin.AdminUserService
import com.app.appellas.data.network.services.user.BlogService
import com.app.appellas.data.network.services.user.ContactService
import com.app.appellas.data.network.services.user.LocationService
import com.app.appellas.data.network.services.user.LoginService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val API_BASE_URL = "https://is-app-ellas.herokuapp.com/api/"

private var interceptor =HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

object AppAPI {
    val loginService: LoginService = retrofit.create(LoginService::class.java)
    val blogService: BlogService = retrofit.create(BlogService::class.java)
    val locationService: LocationService = retrofit.create(LocationService::class.java)
    val contactService: ContactService = retrofit.create(ContactService::class.java)
    val adminUserServices: AdminUserService = retrofit.create(AdminUserService::class.java)
    val adminAdvisorServices: AdminAdvisoryService = retrofit.create(AdminAdvisoryService::class.java)
    val adminLocationServices: AdminLocationServices = retrofit.create(AdminLocationServices::class.java)
    val widgetService: WidgetService = retrofit.create(WidgetService::class.java)
}

/*const val API_BASE_URL = "http://api-subastas.latmobile.com:8080/api/"

object RetrofitInstance {
    private val interceptorLogging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private var token = ""

    fun setToken(value: String) {
        token = value
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(
            OkHttpClient()
                .newBuilder()
                .addInterceptor { chain ->
                    chain.proceed(
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    )
                }.addInterceptor(interceptorLogging)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getServices(): AuctionsService {
        return retrofit.create(AuctionsService::class.java)
    }
}*/