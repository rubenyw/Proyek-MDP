package com.example.project

import android.content.Context
import android.widget.Toast

object Mekanisme {
//    var userLoggedIn: UserEntity? = null;
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }

    fun isEmptyField(listField: ArrayList<String>): Boolean {
        for (field in listField) {
            if (field.isEmpty()) return true;
        }
        return false;
    }
}