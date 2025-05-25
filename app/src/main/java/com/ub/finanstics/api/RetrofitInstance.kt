package com.ub.finanstics.api

import com.ub.finanstics.api.models.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // TODO: вернуть айпи http://10.0.2.2:8000 http://109.120.151.146:8000
    private const val BASE_URL = "http://10.0.2.2:8000"

    val api: NetworkService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }
}
