package com.example.selomisto.ui.auth

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selomisto.models.AuthRequest
import com.example.selomisto.models.AuthResponse
import com.example.selomisto.models.RegisterRequest
import com.example.selomisto.repository.AuthRepository
import com.example.selomisto.utils.NetworkResult
import com.example.selomisto.validators.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    val tokenLiveData: LiveData<NetworkResult<AuthResponse>> get() = authRepository.tokenLiveData

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            authRepository.registerUser(registerRequest)
        }
    }

    fun login(authRequest: AuthRequest) {
        viewModelScope.launch {
            authRepository.loginUser(authRequest)
        }
    }

    fun validateCredentials(firstname: String, lastname: String, email: String, password: String, isLogin: Boolean): Pair<Boolean, String> {
        var result = Pair(true, "")
        if(TextUtils.isEmpty(email) || (!isLogin && (TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname))) || TextUtils.isEmpty(password)){
            result = Pair(false, "Please provide the credentials")
        }
        else if(!Validators.isValidEmail(email)){
            result = Pair(false, "Email is invalid")
        }
        else if(!TextUtils.isEmpty(password) && password.length <= 5){
            result = Pair(false, "Password length should be greater than 5")
        }
        return result
    }
}