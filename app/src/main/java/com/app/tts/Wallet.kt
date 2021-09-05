package com.app.tts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Wallet : AppCompatActivity() {

    //private lateinit var Wamount : String
    private lateinit var mAmount : TextView

    //private var walletAmount : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)


        mAmount = findViewById(R.id.amount)
        val id2: DatabaseReference = FirebaseDatabase.getInstance().getReference("Wallet").child("1")

        id2.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                mAmount.text = p0.child("WalletAmount").value.toString()
            }

        })

       // mAmount.text = "PKR-1000"


       // val uid = FirebaseAuth.getInstance().currentUser!!.uid
      //  var database = FirebaseDatabase.getInstance().reference
        //database.setValue(mAmount.text)
        //database.child("walletAmount").value.to


}
}