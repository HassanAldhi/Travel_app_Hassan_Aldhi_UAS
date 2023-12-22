package com.example.travelapp.authentication

import android.content.Context
import android.content.SharedPreferences

class PrefManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences

    companion object{
        private const val PREFS_FILENAME = "AuthAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ID = "id"
        private const val KEY_ROLE = "role"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL= "email"
        private const val KEY_PHONE = "phone"

        @Volatile
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this){
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    init {
        sharedPreferences = context.getSharedPreferences(
            PREFS_FILENAME,
            Context.MODE_PRIVATE)
    }

    fun setLoggedIn(isLoggedIn: Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveId(id: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ID, id)
        editor.apply()
    }

    fun getId(): String {
        return sharedPreferences.getString(KEY_ID, "") ?: ""
    }

    fun saveUsername(username: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    fun saveEmail(email: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun savePhone(phone: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PHONE, phone)
        editor.apply()
    }

    fun getPhone(): String {
        return sharedPreferences.getString(KEY_PHONE, "") ?: ""
    }

    fun saveRole(role: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getRole(): String {
        return sharedPreferences.getString(KEY_ROLE, "") ?: ""
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}