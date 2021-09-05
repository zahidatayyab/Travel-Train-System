package com.app.tts.session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.app.tts.DashboardActivity
import com.app.tts.UserLogin

class LoginPref {

    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var con: Context
    var PRIVATEMODE : Int = 0

    constructor(con: Context)
    {
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATEMODE)
        editor = pref.edit()

    }

    companion object{
        val PREF_NAME = "Login_Preference"
        val IS_LOGIN = "isLoggedin"
        val KEY_PASSWORD = "password"
        val KEY_EMAIL = "email"

    }

    fun createLoginSession(email: String, password: String)
    {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()

    }
    /*fun LogoutUser()
    {
        editor.clear()
        editor.commit()
        val i = Intent(con, UserLogin::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity((i))
    }*/
    fun isLoggedin(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }



}