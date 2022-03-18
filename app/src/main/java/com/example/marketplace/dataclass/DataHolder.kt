package com.example.marketplace.dataclass

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class DataHolder {
    private lateinit var sharedPreferences: SharedPreferences
    fun setString(context: Context, key: String, data: String){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val spe = sharedPreferences.edit()
        spe.putString(key, data)
        spe.apply()
    }
    fun getString( context:Context,  key:String): String? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, null)
    }

    fun setSetString( context:Context,  key:String,  data:ArrayList<String>){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val spe = sharedPreferences.edit()
        val hashset = HashSet<String>(data)
        spe.putStringSet(key, hashset)
        spe.apply()
    }
    fun getSetString( context:Context,  key:String):ArrayList<String>{
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val hashset = sharedPreferences.getStringSet(key, null) as HashSet<String>
        return ArrayList(hashset)
    }
}