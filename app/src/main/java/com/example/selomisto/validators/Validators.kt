package com.example.selomisto.validators

import android.text.TextUtils
import android.util.Patterns

class Validators {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}