package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class UserSignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var data: Users
    private lateinit var reference : DatabaseReference

    private lateinit var  signUpProgressBar : ProgressBar       // for displaying progress bar
    private lateinit var userEmail : com.google.android.material.textfield.TextInputEditText     // for getting email from screen
    private lateinit var userPassword : com.google.android.material.textfield.TextInputEditText   // for getting password from screen
    private lateinit var userName : com.google.android.material.textfield.TextInputEditText     // for getting name from screen
    private lateinit var userPhone : com.google.android.material.textfield.TextInputEditText    // for getting phone from screen
    private lateinit var termsConditions : CheckBox     // for getting checkbox value

    private val PASSWORDPATTERN : Pattern =     // a predefined pattern that ensures 1 number, 1 upper case, 1 lowere case and min 6 char
        Pattern.compile("^"+
                "(?=.*[0-9])"+
                "(?=.*[a-z])"+
                "(?=.*[A-Z])"+
                ".{6,}" +
                "$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sign_up)

        signUpProgressBar = findViewById(R.id.signUpProgressBar)
        userEmail = findViewById(R.id.userEmail)
        userPassword= findViewById(R.id.userPassword)
        userName=findViewById(R.id.userName)
        userPhone=findViewById(R.id.userPhone)
        termsConditions = findViewById(R.id.termsConditions)

        auth = Firebase.auth            // getting firebase authenticator
        signUpProgressBar.visibility = View.INVISIBLE   // hiding the progress bar
    }

    private fun validateEmail(emailInput: String) :Boolean  // a function for validating email
    {
        if(emailInput.isEmpty()) {
            userEmail.error = "Email Required"
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            userEmail.error = "Please enter a valid email address"
            return false
        }
        else
            userEmail.error=null
        return true
    }

    private fun validatePassword(passwordInput : String) :Boolean   // a funstion for validating password
    {
        if(passwordInput.isEmpty()) {
            userPassword.error = "Password is Required."
            return false
        }

        else if(!PASSWORDPATTERN.matcher(passwordInput).matches())
        {
            userPassword.error="Password must contain 1 uppercase, 1 lower case, 1 number and should be of length >=6."
            return false
        }

        else
            if(!termsConditions.isChecked)
            {
                val myToast = Toast.makeText(
                    applicationContext,
                    "Please check the terms and conditions.",
                    Toast.LENGTH_SHORT
                )
                myToast.show()
                termsConditions.error=""
                return false
            }

            else
                userPassword.error=null
        termsConditions.error=null
        return true
    }

    private fun validateUsername(nameInput: String) :Boolean    // a function for validating username
    {
        when {
            nameInput.length>15 -> {
                userName.error="Username too long"
                return false
            }
            nameInput.isEmpty() -> {
                userName.error="Username required"
                return false
            }
            else -> userName.error=null
        }
        return true
    }


    fun handleSignUpButton(v: View)             // signup button click handler
    {
        val emailInput: String = userEmail.text.toString()          //
        val passwordInput: String = userPassword.text.toString()    //
        val userNameInput: String = userName.text.toString()        //------------------>>> getting values
        val phoneNumber: String = userPhone.text.toString()         //


        if(validateUsername(userNameInput) && validateEmail(emailInput) && validatePassword(passwordInput)) {   // validating fields

            signUpProgressBar.visibility = View.VISIBLE     // displaying progressbar


            auth.createUserWithEmailAndPassword(emailInput, passwordInput )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser     //getting current user
                        val id= user!!.uid      //getting current user id

                        data = Users(userNameInput,phoneNumber,5.0, "false","User") //an object for data

                        reference= Firebase.database.reference.child("Users")   // getting the required root
                        reference.child(id).setValue(data)  // pushing or saving the newly created data


                        user.sendEmailVerification()        // sending email verification code
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(applicationContext,"Please verify your email address to sign in the app",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }

                        val intent = Intent(baseContext, UserLogin::class.java)     // moving to login screen
                        startActivity((intent))
                        finish()
                    }
                    else        // in-case of error or invalid format used
                    {
                        Toast.makeText(applicationContext,"Authentication error or email address already in use",
                            Toast.LENGTH_SHORT).show()
                        signUpProgressBar.visibility = View.INVISIBLE
                    }
                }
        }

        else // in-case of error or invalid format used
        {
            val toast: Toast = Toast.makeText(applicationContext,"Authentication Failed.",
                Toast.LENGTH_SHORT)
            toast.show()

            signUpProgressBar.visibility = View.INVISIBLE
        }

    }

    fun handleAlreadyAccountButton(v:View)
    {
        val intent = Intent(baseContext, UserLogin::class.java)
        startActivity((intent))
        finish()
    }
}