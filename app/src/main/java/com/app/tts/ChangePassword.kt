package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePassword : AppCompatActivity() {
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        password=findViewById(R.id.updatePassword)
        confirmPassword=findViewById(R.id.updateConfirmPassword)
    }

    fun handleChangePasswordBackButton(v: View)
    {
        val intent = Intent(applicationContext,UserSettings::class.java)
        startActivity(intent)
    }

    fun handleUpdatePasswordButton(v:View)
    {
        if(password.text.isNotEmpty() && confirmPassword.text.isNotEmpty()
            && password.text.toString()==confirmPassword.text.toString())
        {
            val user = Firebase.auth.currentUser
            val newPassword = password.text.toString()

            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext,"Password Updated",Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext,UserSettings::class.java)
                        startActivity(intent)
                    }

                    else
                    {
                        Toast.makeText(applicationContext,"Error try again",Toast.LENGTH_SHORT).show()
                    }
                }
        }

        else {
            Toast.makeText(applicationContext, "Password is empty or do not match", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleDiscardPasswordButton(v:View)
    {
        val intent = Intent(applicationContext,UserSettings::class.java)
        startActivity(intent)
    }
}