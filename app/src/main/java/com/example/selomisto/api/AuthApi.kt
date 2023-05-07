package com.example.selomisto.api

import com.example.selomisto.models.AuthRequest
import com.example.selomisto.models.AuthResponse
import com.example.selomisto.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("auth/authenticate")
    suspend fun auth(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("auth/authenticate-google")
    suspend fun authWithGoogle(@Body authRequest: RegisterRequest): Response<AuthResponse>

}