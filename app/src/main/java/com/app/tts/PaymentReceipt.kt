package com.app.tts

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class PaymentReceipt : AppCompatActivity() {
    private lateinit var car1PickAdd : TextView
    private lateinit var car1DropAdd : TextView
    private lateinit var driver1Name : TextView
    private lateinit var car1License : TextView
    private lateinit var payment1 : TextView
    private lateinit var pickup1 : TextView
    private lateinit var dropOff1 : TextView

    private lateinit var car1PickAdd1 : TextView
    private lateinit var car1DropAdd1 : TextView
    private lateinit var driver1Name1 : TextView
    private lateinit var car1License1 : TextView
    private lateinit var payment11 : TextView
    private lateinit var pickup11 : TextView
    private lateinit var dropOff11 : TextView

    private lateinit var car1PickAdd2 : TextView
    private lateinit var car1DropAdd2 : TextView
    private lateinit var driver1Name2 : TextView
    private lateinit var car1License2 : TextView
    private lateinit var payment12 : TextView
    private lateinit var pickup12 : TextView
    private lateinit var dropOff12 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_receipt)

        car1DropAdd = findViewById(R.id.car1DropAdd)
        car1PickAdd = findViewById(R.id.car1PickAdd)
        driver1Name = findViewById(R.id.driver1Name)
        car1License = findViewById(R.id.car1License)
        payment1 = findViewById(R.id.payment1)
        pickup1 = findViewById(R.id.pickup1)
        dropOff1 = findViewById(R.id.dropOff1)

        car1DropAdd1 = findViewById(R.id.car1DropAdd1)
        car1PickAdd1 = findViewById(R.id.car1PickAdd1)
        driver1Name1 = findViewById(R.id.driver1Name1)
        car1License1 = findViewById(R.id.car1License1)
        payment11 = findViewById(R.id.payment11)
        pickup11 = findViewById(R.id.pickup11)
        dropOff11 = findViewById(R.id.dropOff11)

        car1DropAdd2 = findViewById(R.id.car1DropAdd2)
        car1PickAdd2 = findViewById(R.id.car1PickAdd2)
        driver1Name2 = findViewById(R.id.driver1Name2)
        car1License2 = findViewById(R.id.car1License2)
        payment12 = findViewById(R.id.payment12)
        pickup12 = findViewById(R.id.pickup12)
        dropOff12 = findViewById(R.id.dropOff12)

        val id21: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("2")

        id21.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                car1License2.text = p0.child("LicensePlate").value.toString()
                driver1Name2.text = p0.child("Name").value.toString()
            }

        })

        val id2: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("1")

        id2.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                car1License.text = p0.child("LicensePlate").value.toString()
                driver1Name.text = p0.child("Name").value.toString()
            }

        })

        val id4: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverVan")

        id4.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                car1License1.text = p0.child("License").value.toString()
                driver1Name1.text = p0.child("Name").value.toString()
            }

        })

        var lat = 0.0
        var lon = 0.0

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val id3: DatabaseReference = FirebaseDatabase.getInstance().getReference("RealTimeDataBase").child(uid)

        id3.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                lat = p0.child("carPickuplat").value.toString().toDouble()
                lon = p0.child("carPickuplon").value.toString().toDouble()

                car1PickAdd.text = getCompleteAddressString(lat, lon)

                lat = p0.child("carDropOffLat").value.toString().toDouble()
                lon = p0.child("carDropOffLon").value.toString().toDouble()

                car1DropAdd.text = getCompleteAddressString(lat,lon)
                payment1.text = p0.child("carFare").value.toString()
                payment11.text = p0.child("vanFare").value.toString()
                payment12.text = p0.child("carFare2").value.toString()
                pickup1.text = p0.child("carPickupTime").value.toString()
                pickup12.text = p0.child("carPickupTime2").value.toString()
                pickup11.text = p0.child("vanPickUpTime").value.toString()
                dropOff11.text = p0.child("vanDropOffTime").value.toString()
                dropOff1.text = p0.child("carDropOffTime").value.toString()

                dropOff1.text = p0.child("carDropOffTime").value.toString()

                dropOff12.text = p0.child("carDropOffTime2").value.toString()

                car1PickAdd1.text = getCompleteAddressString(lat,lon)

                lat = p0.child("vanDropOffLat").value.toString().toDouble()
                lon = p0.child("vanDropOffLon").value.toString().toDouble()

                car1DropAdd1.text = getCompleteAddressString(lat,lon)

                lat = p0.child("carDropOffLat2").value.toString().toDouble()
                lon = p0.child("carDropOffLon2").value.toString().toDouble()

                car1DropAdd2.text = getCompleteAddressString(lat,lon)

                car1PickAdd2.text = car1DropAdd1.text
            }

        })

    }

    private fun getCompleteAddressString(   // function for getting address against lat/lon
        LATITUDE: Double,
        LONGITUDE: Double
    ) : String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
            }
            else
            {
                Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {

        }

        return strAdd
    }

    fun handleDetailsViewRouteNext(v: View)
    {
        val intent = Intent(applicationContext,DashboardActivity::class.java)
        startActivity(intent)
        finish()

    }

}