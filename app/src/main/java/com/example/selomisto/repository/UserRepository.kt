package com.example.selomisto.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.selomisto.api.UserApi
import com.example.selomisto.models.User
import com.example.selomisto.utils.NetworkResult
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi) {

    private val _userLiveData = MutableLiveData<NetworkResult<User>>()
    val userLiveData: LiveData<NetworkResult<User>> get() =_userLiveData

    private val _userListLiveData = MutableLiveData<NetworkResult<List<User>>>()
    val userListLiveData: LiveData<NetworkResult<List<User>>> get() = _userListLiveData

    suspend fun getAllUsers() {
        val response = userApi.getAllUsers()
        handleUserListResponse(response)
    }

    suspend fun getUserById(id: Long) {
        val response = userApi.getUserById(id)
        handleUserResponse(response)
    }

    private fun handleUserResponse(response: Response<User>) {
        if (response.isSuccessful && response.body() != null) {
            _userLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

    private fun handleUserListResponse(response: Response<List<User>>) {
        if (response.isSuccessful && response.body() != null) {
            _userListLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userListLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userListLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }


}