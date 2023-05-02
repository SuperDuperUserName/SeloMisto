package com.example.selomisto.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selomisto.models.User
import com.example.selomisto.repository.UserRepository
import com.example.selomisto.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    val userLiveData: LiveData<NetworkResult<User>> get() = userRepository.userLiveData
    val userListLiveData: LiveData<NetworkResult<List<User>>> get() = userRepository.userListLiveData

    fun getUserById(id: Long) {
        viewModelScope.launch {
            userRepository.getUserById(id)
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers()
        }
    }
}