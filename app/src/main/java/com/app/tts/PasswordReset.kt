package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class PasswordReset : AppCompatActivity() {
    private lateinit var UserForgotEmail : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        UserForgotEmail = findViewById(R.id.UserForgotEmail)
    }

    private fun validateEmail(emailInput: String) :Boolean
    {
        if(emailInput.isEmpty())
        {
            UserForgotEmail.error = "Email Required"
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return false
        else
            return true
    }

    fun handleSendEmailButton(v: View)
    {
        val mAuth = FirebaseAuth.getInstance()

        if(validateEmail(UserForgotEmail.text.toString()))
        {
            mAuth.sendPasswordResetEmail(UserForgotEmail.text.toString())
            Toast.makeText(baseContext,"Please check your email", Toast.LENGTH_SHORT).show()
        }

        else
            Toast.makeText(baseContext,"Try again later", Toast.LENGTH_SHORT).show()

        val intent = Intent(applicationContext,UserLogin::class.java)
        startActivity(intent)

    }
}