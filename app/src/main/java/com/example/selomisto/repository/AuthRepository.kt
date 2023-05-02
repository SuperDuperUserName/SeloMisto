package com.example.selomisto.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.selomisto.api.AuthApi
import com.example.selomisto.models.AuthRequest
import com.example.selomisto.models.AuthResponse
import com.example.selomisto.models.RegisterRequest
import com.example.selomisto.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApi: AuthApi) {

    private val _tokenLiveData = MutableLiveData<NetworkResult<AuthResponse>>()
    val tokenLiveData: LiveData<NetworkResult<AuthResponse>> get() = _tokenLiveData

    suspend fun registerUser(registerRequest: RegisterRequest) {
        val response = authApi.register(registerRequest)
        handleToken(response)
    }

    suspend fun loginUser(authRequest: AuthRequest) {
        val response = authApi.auth(authRequest)
        handleToken(response)
    }

    private fun handleToken(response: Response<AuthResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _tokenLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _tokenLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _tokenLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}