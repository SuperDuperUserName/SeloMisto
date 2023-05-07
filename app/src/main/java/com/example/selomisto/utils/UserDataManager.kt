package com.example.selomisto.utils

import android.content.Context
import com.example.selomisto.models.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserDataManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(Constants.USER_DATA_FILE, Context.MODE_PRIVATE)

    fun setUser(user: User) {
        val editor = prefs.edit()
        editor.putString(Constants.USER, Gson().toJson(user))
        editor.apply()
    }

    fun getUser(): User? {
        return Gson().fromJson(prefs.getString(Constants.USER, null), User::class.java)
    }

}