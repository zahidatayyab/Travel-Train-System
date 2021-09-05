package com.app.tts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserSettings : AppCompatActivity() {
    private  lateinit var userSettingName : TextView
    private  lateinit var userSettingEmail : TextView
    private  lateinit var userSettingPhone : TextView
    private lateinit var auth: FirebaseAuth // firebase authenticator
    private lateinit var reference : DatabaseReference  // firebase database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        userSettingName = findViewById(R.id.userSettingName)
        userSettingEmail = findViewById(R.id.userSettingEmail)
        userSettingPhone = findViewById(R.id.userSettingPhone)

        auth = Firebase.auth    // setting the authenticator
        reference = Firebase.database.reference.child("Users").child(auth.currentUser!!.uid)    // getting the user tree path
        userSettingEmail.text = auth.currentUser!!.email.toString()

        reference.addListenerForSingleValueEvent(object : ValueEventListener {  // fetching user details
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                //Toast.makeText(applicationContext,p0.child("name").value.toString(),Toast.LENGTH_SHORT).show()
                userSettingName.text = p0.child("name").value.toString()
                userSettingPhone.text = p0.child("phone").value.toString()
                //drawerRating.text = p0.child("rating").value.toString()
            }
        })
    }

    fun handleBackButton(v: View)
    {
        val intent = Intent(applicationContext,DashboardActivity::class.java)
        startActivity(intent)
    }

    fun handleChangeUserName(v:View)
    {
        val intent = Intent(applicationContext,UserSettingsUpdate::class.java)
        startActivity(intent);
    }

    fun handleEmailClick(v: View)
    {
        Toast.makeText(applicationContext,"Cannot be changed",Toast.LENGTH_SHORT).show()
    }

    fun handleChangePassword(v: View)
    {
        val intent = Intent(applicationContext,ChangePassword::class.java)
        startActivity(intent);
    }
}