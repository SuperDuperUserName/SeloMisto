package com.example.selomisto.utils

import android.content.Context
import android.util.Log
import com.example.selomisto.utils.Constants.PREFS_TOKEN_FILE
import com.example.selomisto.utils.Constants.TAG
import com.example.selomisto.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun clearToken() {
        if (prefs.contains(USER_TOKEN)) {
            val editor = prefs.edit()
            editor.remove(USER_TOKEN)
            editor.apply()
            Log.d(TAG, "Token was cleared")
        }
    }
}