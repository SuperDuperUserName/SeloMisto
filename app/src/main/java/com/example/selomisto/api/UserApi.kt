package com.example.selomisto.api

import com.example.selomisto.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("user")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

}