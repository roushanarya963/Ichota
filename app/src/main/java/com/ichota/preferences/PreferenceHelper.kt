package com.ichota.preferences

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.ichota.model.User

class PreferenceHelper(val context: Context) {

    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(PrefKeys.KEY_PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        fun getPreferences(context: Context): PreferenceHelper {
            val helper = PreferenceHelper(context)
            return helper
        }
    }

    fun saveCurrentUser( value: User) {
        val gson = Gson()
        val json = gson.toJson(value)
        preferences?.edit()?.putString(PrefKeys.KEY_PREF_USER, json)?.apply()
    }

    fun getCurrentUser(): User? {
        val gson = Gson()
        val json = preferences?.getString(PrefKeys.KEY_PREF_USER, null)
        return gson.fromJson(json, User::class.java)
    }

    fun getLocation(key: String): Location? {
        val gson = Gson()
        val json = preferences?.getString(key, null)
        Log.d("TAG", "saveLocation: ${gson.fromJson(json, Location::class.java)}")
        return gson.fromJson(json, Location::class.java)

    }

    fun saveBoolean(key: String, value: Boolean) {
        preferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return preferences?.getBoolean(key, false) ?: false
    }

    fun saveStringValue(key: String, value: String?) {
        preferences?.edit()?.putString(key, value)?.apply()
    }

    fun getStringValue(key: String): String? {
        return preferences?.getString(key, null)
    }

    fun saveInt(key: String, value: Int) {
        preferences?.edit()?.putInt(key, value)?.apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences?.getInt(key, defaultValue) ?: defaultValue
    }


    fun saveproductIdString(key: String,value: String){
        preferences?.edit()?.putString(key,value)?.apply()
    }
    fun getproductIdString(key: String,defaultValue: String=" "):String{
      return  preferences?.getString(key,defaultValue)?:defaultValue
    }


    fun savebidAmountInt(key:String, value: Int){
        preferences?.edit()?.putInt(key,value)?.apply()
    }
    fun getsavebidAmountInt(key:String,defaultValue: Int=0):Int{
        return preferences?.getInt(key,defaultValue)?:defaultValue
    }
}