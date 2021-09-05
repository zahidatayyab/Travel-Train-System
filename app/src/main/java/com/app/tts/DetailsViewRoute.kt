package com.app.tts

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DetailsViewRoute : AppCompatActivity()  {
    private var country : String = ""   // for storing country
    private var city : String = ""      // for storing city

    private lateinit var CarPickupAdd : TextView
    private lateinit var DetailsDate : TextView
    private lateinit var carDropOff : TextView
    private lateinit var carDistanceTime : TextView
    private lateinit var carCharges : TextView
    private lateinit var carTime : TextView
    private lateinit var vanPickUp : TextView
    private lateinit var vanDropOff : TextView
    private lateinit var VanDistanceTime : TextView
    private lateinit var VanPickupTime : TextView
    private lateinit var CarLicense : TextView
    private lateinit var CarPickupAdd2 : TextView
    private lateinit var carDropOff2 : TextView
    private lateinit var carTime2 : TextView
    private lateinit var carDistanceTime2 : TextView
    private lateinit var carCharges2 : TextView
    private lateinit var CarLicense2 : TextView
    private lateinit var  request: com.app.tts.Request

    private var  lat1 : Double = 0.0
    private var  lon1 : Double = 0.0
    private var lat2 : Double = 0.0
    private var  lon2 : Double = 0.0
    private var  alat1 : Double = 0.0
    private var  alon1 : Double = 0.0
    private var alat2 : Double = 0.0
    private var  alon2 : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_view_route)

        CarPickupAdd = findViewById(R.id.CarPickupAdd)
        CarPickupAdd2 = findViewById(R.id.CarPickupAdd2)
        carDropOff = findViewById(R.id.carDropOff)
        DetailsDate = findViewById(R.id.DetailsDate)
        carDistanceTime = findViewById(R.id.carDistanceTime)
        carCharges = findViewById(R.id.carCharges)
        carTime = findViewById(R.id.carTime)
        vanPickUp = findViewById(R.id.vanPickUp)
        vanDropOff = findViewById(R.id.vanDropOff)
        VanDistanceTime = findViewById(R.id.VanDistanceTime)
        VanPickupTime = findViewById(R.id.VanPickupTime)
        CarLicense = findViewById(R.id.CarLicense)
        carDropOff2 = findViewById(R.id.carDropOff2)
        carTime2 = findViewById(R.id.carTime2)
        carDistanceTime2 = findViewById(R.id.carDistanceTime2)
        carCharges2 = findViewById(R.id.carCharges2)
       CarLicense2 = findViewById(R.id.CarLicense2)

        val ext : Bundle? = intent.extras
        lat1 = ext?.getDouble("lat1")!!       // fetching the lat/lon passed from previous screens
        lon1 = ext.getDouble("lon1")
        lat2 = ext.getDouble("lat2")
        lon2 = ext.getDouble("lon2")
        alon1 = ext.getDouble("alon1")
        alat1 = ext.getDouble("alat1")
        alon2 = ext.getDouble("alon2")
        alat2 = ext.getDouble("alat2")

        //Toast.makeText(applicationContext, "$alat1 $alon1 $hour $min",Toast.LENGTH_SHORT).show()
        carPickUp(lat1,lon1)
        carDropOff(alat1,alon1)

        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin="+lat1+","+lon1+"&destination="+alat1+","+alon1+"&key=AIzaSyBPuIYB7MzH03waYZ1OTQ9RVNv2lX_A_U8"

        //getting the direction data in the form of json and then converting it
        val directionsRequest =
        object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            val distance = legs.getJSONObject(0).getJSONObject("distance").getString("text")
            val time = legs.getJSONObject(0).getJSONObject("duration").getString("text")
            carDistanceTime.text="$distance $time"
            carCharges.text = "100 Rs"

            //drawing the lines against the route on the map
        }, Response.ErrorListener {
                _ ->
            Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)

        vanPickUp.text = getCompleteAddressString(alat1,alon1)
        vanDropOff.text = getCompleteAddressString(alat2,alon2)

        val urlDirections2 = "https://maps.googleapis.com/maps/api/directions/json?origin="+alat1+","+alon1+"&destination="+alat2+","+alon2+"&key=AIzaSyBPuIYB7MzH03waYZ1OTQ9RVNv2lX_A_U8"

        //getting the direction data in the form of json and then converting it
        val directionsRequest2 =
            object : StringRequest(Request.Method.GET, urlDirections2, Response.Listener<String> {
                    response ->
                val jsonResponse = JSONObject(response)
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")

                val distance = legs.getJSONObject(0).getJSONObject("distance").getString("text")
                val time = legs.getJSONObject(0).getJSONObject("duration").getString("text")
                VanDistanceTime.text="$distance $time"

                //drawing the lines against the route on the map
            }, Response.ErrorListener {
                    _ ->
                Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
            }){}
        val requestQueue2 = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest2)


        @SuppressLint("ConstantLocale")
        val currentDate: String = SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault()).format(
            Date()
        )


        val date = Calendar.getInstance();
        val t= date.timeInMillis

        val carpickupTime = Date(t)
        val carDropOffTime = Date(t + (10*60000))
        val vanpickupTime = Date(t + (25*60000))
        val vanDropOffTime = Date(t + (60*60000))
        val carpickupTime2 = Date(t + (65*60000))
        val carDropOffTime2 = Date(t + (75*60000))


        carTime.text = carpickupTime.toString()
        VanPickupTime.text =vanpickupTime.toString()


        val id: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("1")

        id.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                CarLicense.text = p0.child("LicensePlate").value.toString()
            }

        })


        CarPickupAdd2.text = vanDropOff.text
        carDropOff2.text =getCompleteAddressString(lat2,lon2)
        carTime2.text = carpickupTime2.toString()
        carCharges2.text = "50 Rs"

        val urlDirections3 = "https://maps.googleapis.com/maps/api/directions/json?origin="+alat2+","+alon2+"&destination="+lat2+","+lon2+"&key=AIzaSyBPuIYB7MzH03waYZ1OTQ9RVNv2lX_A_U8"

        //getting the direction data in the form of json and then converting it
        val directionsRequest3 =
            object : StringRequest(Request.Method.GET, urlDirections3, Response.Listener<String> {
                    response ->
                val jsonResponse = JSONObject(response)
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")

                val distance = legs.getJSONObject(0).getJSONObject("distance").getString("text")
                val time = legs.getJSONObject(0).getJSONObject("duration").getString("text")
                carDistanceTime2.text="$distance $time"

                //drawing the lines against the route on the map
            }, Response.ErrorListener {
                    _ ->
                Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
            }){}
        val requestQueue3 = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest3)


        val id2: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("2")

        id2.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                CarLicense2.text = p0.child("LicensePlate").value.toString()
            }

        })

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        request = Request(alat1.toString(),alon1.toString(),lat1.toString(),lon1.toString(),
        carpickupTime.toString(),carDropOffTime.toString(), alat1.toString(),alon1.toString(),alat2.toString(),alon2.toString(),
        vanpickupTime.toString(),vanDropOffTime.toString(), lat2.toString(), lon2.toString(), carpickupTime2.toString(),
            carDropOffTime2.toString(),"100","60","50",
            currentDate,"W",uid.toString(),"1","2")

        date(1,2,3)
    }

    @SuppressLint("SetTextI18n")
    fun date(day : Int, month : Int, Year : Int)
    {
        val date = Calendar.getInstance()
        val t= date.timeInMillis
        DetailsDate.text = Date(t).toString()
    }

    private fun carDropOff(lat1: Double, lon1: Double)
    {
        carDropOff.text = getCompleteAddressString(lat1,lon1)
    }

    private fun carPickUp(lat1: Double, lon1: Double)
    {
        CarPickupAdd.text = getCompleteAddressString(lat1,lon1)
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
                country= addresses[0].countryName
                city = addresses[0].locality

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
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val mDatabase = FirebaseDatabase.getInstance().getReference("RealTimeDataBase")
        mDatabase.child(uid).setValue(request)

        val intent = Intent(applicationContext,EconomyRoute::class.java)
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


    fun cancelation(v:View)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm")
            .setMessage("Are you sure you want to Cancel booking")
            .setNegativeButton("No") { dialog, which ->
                //Toast.makeText(applicationContext,"Done", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext,DetailsViewRoute::class.java)
            }
            .setPositiveButton("Yes") { dialog, which ->
                val intent = Intent(applicationContext,DashboardActivity::class.java)
                Toast.makeText(applicationContext,"Ride Cancelled Successfully.", Toast.LENGTH_SHORT).show()
                startActivity(intent)




            }
            .show()


    }




}