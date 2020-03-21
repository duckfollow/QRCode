package me.duckfollow.qrcode.user

import android.content.Context
import android.content.SharedPreferences

class UserProfile {
    var User: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val USER_PREFS = "USER_PREFS"

    constructor(context: Context) {
        User = context!!.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    fun setDateUser(date:String){
        this.setUserData("user_date", date)
    }

    fun getDateUser():String{
        return User.getString("user_date","")
    }

    fun setUserData(key:String,value:String){
        editor = User.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getUserData(key:String):String{
        return User.getString(key,"")
    }
}