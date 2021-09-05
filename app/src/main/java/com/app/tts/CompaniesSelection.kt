package com.app.tts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.Dash
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class CompaniesSelection : AppCompatActivity() {

    private var  lat1 : Double = 0.0
    private var  lon1 : Double = 0.0
    private var lat2 : Double = 0.0
    private var  lon2 : Double = 0.0
    private var  alat1 : Double = 0.0
    private var  alon1 : Double = 0.0
    private var alat2 : Double = 0.0
    private var  alon2 : Double = 0.0
    private var kilometers : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_companies_selection)

        alat1 = 31.47179668030714
        alon1 = 74.24295436088241

        alat2 = 31.557107388639363
        alon2 = 74.36350323969545

        val ext : Bundle? = intent.extras
        kilometers = ext?.getString("kilometers").toString()
        lat1 = ext?.getDouble("lat1")!!       // fetching the lat/lon passed from previous screens
        lon1 = ext.getDouble("lon1")
        lat2 = ext.getDouble("lat2")
        lon2 = ext.getDouble("lon2")

    }

    fun handleSelectCompanyBackButton(v: View)
    {
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        startActivity(intent)
    }

    fun handleEconomyCaseButton(v:View)
    {
        val intent = Intent(applicationContext,DateTimeSchedule::class.java)
        intent.putExtra("kilometers",kilometers)
        intent.putExtra("lon1",lon1)
        intent.putExtra("lat1",lat1)
        intent.putExtra("lon2",lon2)
        intent.putExtra("lat2",lat2)
        intent.putExtra("alon1",alon1)
        intent.putExtra("alat1",alat1)
        intent.putExtra("alon2",alon2)
        intent.putExtra("alat2",alat2)

        startActivity(intent)

    }

}