package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserSettingsUpdate : AppCompatActivity() {
    private lateinit var username : EditText
    private lateinit var phoneNumber : EditText
    private lateinit var auth: FirebaseAuth // firebase authenticator
    private lateinit var reference : DatabaseReference  // firebase database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings_update)

        username = findViewById(R.id.updateUsername)
        phoneNumber = findViewById(R.id.updatePhoneNumber)

        auth = Firebase.auth    // setting the authenticator
        reference = Firebase.database.reference.child("Users").child(auth.currentUser!!.uid)    // getting the user tree path
    }

    fun handleUpdateButton(v: View)
    {
        if(username.text.toString().isEmpty())
        {
            if(phoneNumber.text.toString().isEmpty())
                Toast.makeText(baseContext,"Nothing to save", Toast.LENGTH_SHORT).show()
            if (phoneNumber.text.toString().isNotEmpty())
            {
                reference.child("phone").setValue(phoneNumber.text.toString())

                Toast.makeText(baseContext,"Phone Updated", Toast.LENGTH_SHORT).show()
            }
        }

        if(username.text.toString().isNotEmpty()) {
            if (phoneNumber.text.toString().isEmpty()) {
                reference.child("name").setValue(username.text.toString())

                Toast.makeText(baseContext, "name Updated", Toast.LENGTH_SHORT).show()
            }

            if (phoneNumber.text.toString().isNotEmpty()) {
                reference.child("name").setValue(username.text.toString())
                reference.child("phone").setValue(phoneNumber.text.toString())

                Toast.makeText(baseContext, "Phone & name Updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleBackButton(v: View)
    {
        val intent = Intent(applicationContext,UserSettings::class.java)
        startActivity(intent)
    }
}