package com.app.tts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.app.tts.session.LoginPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class UserLogin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth                 // firebase authenticator
    private lateinit var reference : DatabaseReference      // database

    private lateinit var userEmail : com.google.android.material.textfield.TextInputEditText      //
    private lateinit var userPassword : com.google.android.material.textfield.TextInputEditText    //   used for fetching from front end
    private lateinit var  loginProgressBar : ProgressBar                                        //

    private lateinit var userEmailInput: String
    private lateinit var userPasswordInput: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        loginProgressBar = findViewById(R.id.loginProgressBar)  //
        loginProgressBar.visibility = View.INVISIBLE            //      hiding the progress bar

        userEmail = findViewById(R.id.userLoginEmail)       //
        userPassword = findViewById(R.id.userLoginPassword) //  getting the text fields

        auth = Firebase.auth    // getting the authenticator



        //val sharePref = this.getPreferences(Context.MODE_PRIVATE)?:return
        //val spEmail = sharePref.getString("Email", "1")







    }

    private fun validateEmail(emailInput: String) :Boolean  // validating email
    {
        if(emailInput.isEmpty())
            return false


        if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return false

        return true
    }

    private fun validatePassword(passwordInput : String) :Boolean {     // validating password
        if (passwordInput.isEmpty()) {
            return false
        }
        return true
    }

    private fun getEmailPassword()      // fetching data into local variable from text fields
    {
        userEmailInput=userEmail.text.toString()
        userPasswordInput=userPassword.text.toString()
    }


    fun handleLoginButton(v:View)
    {
        getEmailPassword()

        if(!validateEmail(userEmailInput))                                  // validating email and password
            Toast.makeText(applicationContext,"Please enter a valid email address", Toast.LENGTH_SHORT).show()
        else if(!validatePassword(userPasswordInput))
            Toast.makeText(applicationContext,"Please check your password", Toast.LENGTH_SHORT).show()
        else {

            loginProgressBar.visibility= View.VISIBLE               // shwoing progressbar

            auth.signInWithEmailAndPassword(userEmailInput, userPasswordInput)      // function for sign in provided by firebase
                .addOnCompleteListener(this)
                { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser?.isEmailVerified  // checking for email verification
                        //var verified : String = ""

                        if (user != null)  // if user email is verified
                        {
                            if (user)
                            {
                                var type : String = ""

                                auth = Firebase.auth

                                reference = Firebase.database.reference.child("Users").child(auth.currentUser!!.uid) // getting data

                                reference.addListenerForSingleValueEvent(object :   // from firebase
                                    ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        Toast.makeText(baseContext,p0.message, Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        type = p0.child("accountType").value.toString()

                                        if(type=="User")        // checking account type
                                        {
                                            val intent = Intent(baseContext, DashboardActivity::class.java)
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity((intent))
                                            finish()
                                        }
                                        else    // if account type other than user e.g. driver
                                        {
                                            auth.signOut()

                                            Toast.makeText(baseContext,"Please check your email or password",
                                                Toast.LENGTH_SHORT).show()

                                            loginProgressBar.visibility = View.INVISIBLE
                                        }
                                    }
                                })
                            }
                            else
                            {
                                Toast.makeText(
                                    applicationContext,
                                    "Please verify your email",
                                    Toast.LENGTH_SHORT
                                ).show()

                                loginProgressBar.visibility = View.INVISIBLE
                            }
                        }


                    }
                    else
                        Toast.makeText(
                            applicationContext,
                            "Please check your email and password.",
                            Toast.LENGTH_SHORT
                        ).show()

                    loginProgressBar.visibility = View.INVISIBLE

                }
        }

    }

    fun handleSignUpNowButton(v: View)  // moving to dashboard
    {
        val intent = Intent(baseContext, UserSignUp::class.java)
        startActivity((intent))
    }

    fun handleresetpassword(v:View)
    {
        val intent = Intent(baseContext, PasswordReset::class.java)
        startActivity((intent))
    }
}