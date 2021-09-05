package com.app.tts

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EconomyRoute : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var VanPickupTime : TextView
    private lateinit var carDropOff : TextView
    private lateinit var DriverName : TextView
    private lateinit var RouteTitle : TextView
    private lateinit var DriverMobile : TextView
    private lateinit var License : TextView
    private lateinit var seatNo : TextView
    private lateinit var mMap: GoogleMap
    private lateinit var myMarker : Marker

    private var  lat1 : Double = 0.0
    private var  lon1 : Double = 0.0
    private var lat2 : Double = 0.0
    private var  lon2 : Double = 0.0
    private var  alat1 : Double = 0.0
    private var  alon1 : Double = 0.0
    private var alat2 : Double = 0.0
    private var  alon2 : Double = 0.0

    private lateinit var amount1 : String
    private lateinit var amount2 : String
    private lateinit var amount3 : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_economy_route)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapRide) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(applicationContext, getString(R.string.Places_Api))
        VanPickupTime = findViewById(R.id.VanPickupTime)
        carDropOff = findViewById(R.id.carDropOff)
        DriverName = findViewById(R.id.DriverName)
        DriverMobile = findViewById(R.id.DriverMobile)
        RouteTitle = findViewById(R.id.RouteTitle)
        License = findViewById(R.id.License)

        val ext : Bundle? = intent.extras
        var kilometers = ext?.getString("kilometers").toString()
        lat1 = ext?.getDouble("lat1")!!
        lon1 = ext.getDouble("lon1")
        lat2 = ext.getDouble("lat2")
        lon2 = ext.getDouble("lon2")
        alon1 = ext.getDouble("alon1")
        alat1 = ext.getDouble("alat1")
        alon2 = ext.getDouble("alon2")
        alat2 = ext.getDouble("alat2")

        var state = "W"

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val id2: DatabaseReference = FirebaseDatabase.getInstance().getReference("RealTimeDataBase").child(uid)

        id2.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                amount1 = p0.child("carFare").value.toString()
                amount2 = p0.child("vanFare").value.toString()
                amount3 = p0.child("carFare2").value.toString()
            }

        })


        val id3: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("1")

        id3.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("User App",p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                DriverName.text= p0.child("Name").value.toString()
                DriverMobile.text= p0.child("Mobile").value.toString()
                License.text= p0.child("LicensePlate").value.toString()
            }

        })

        val dbRef = FirebaseDatabase.getInstance().reference.child("RealTimeDataBase")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.i("TTS_APP1", p0.message)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.i("TTS_APP1", "Request moved or deleted")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.i("TTS_APP1", "XXX")

                if(p0.value=="C")
                {
                    RouteTitle.text = "On your Route"


                    var LatLng = LatLng(lat1,lon1)
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(LatLng))!!
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng,18f))
                }

                if(p0.value=="CC")
                {
                    MaterialAlertDialogBuilder(this@EconomyRoute)
                        .setTitle("Payment Receipt:")
                        .setMessage("Please Pay $amount1 to the ${DriverName.text}.")
                        .setNegativeButton("Ok") { dialog, which ->
                        }
                        .show()

                    RouteTitle.text = "Waiting For Airlift."
                    DriverName.text = ""
                    License.text=""
                    DriverMobile.text = ""

                    val db = FirebaseDatabase.getInstance().reference.child("DriverVan")
                    db.addChildEventListener(object : ChildEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.i("TTS_APP1", p0.message)
                        }

                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                            Log.i("TTS_APP1", "Request moved or deleted")
                        }

                        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                            Log.i("TTS_APP1", "Request has been modified")
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            Log.i("TTS_APP1", p0.toString())

                            if(p0.key=="Name")
                                DriverName.text= p0.value.toString()
                            if(p0.key=="Mobile")
                                DriverMobile.text= p0.value.toString()
                            if(p0.key=="License")
                            License.text= p0.value.toString()
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                            Log.i("TTS_APP1", "Request has been deleted")
                        }

                    })

                    val pakistan = LatLng(30.3753 ,  69.3451)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pakistan,18f))
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(pakistan))!!
                }

                if(p0.value=="V")
                {
                    RouteTitle.text = "Enroute to Airlift Stop"
                    var LatLng = LatLng(alat1,alon1)
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(LatLng))!!
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng,18f))

                }
                if(p0.value=="VC")
                {
                    MaterialAlertDialogBuilder(this@EconomyRoute)
                        .setTitle("Payment Receipt:")
                        .setMessage("Please Pay $amount2 to the ${DriverName.text} as airlift fare.")
                        .setNegativeButton("Ok") { dialog, which ->
                        }
                        .show()

                    RouteTitle.text = "Waiting for Mini"

                    val pakistan = LatLng(30.3753 ,  69.3451)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pakistan))
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(pakistan))!!

                    val id3: DatabaseReference = FirebaseDatabase.getInstance().getReference("DriverCar").child("2")

                    id3.addListenerForSingleValueEvent(object : ValueEventListener
                    {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.i("User App",p0.message)
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            DriverName.text= p0.child("Name").value.toString()
                            DriverMobile.text= p0.child("Mobile").value.toString()
                            License.text= p0.child("LicensePlate").value.toString()
                        }
                    })
                }

                if(p0.value=="X")
                {
                    RouteTitle.text = "Enroute to Destination"

                    var LatLng = LatLng(alat2,alon2)
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(LatLng))!!
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng,18f))
                }

                if(p0.value == "XC")
                {
                    MaterialAlertDialogBuilder(this@EconomyRoute)
                        .setTitle("Payment Receipt:")
                        .setMessage("Please Pay $amount3 to the ${DriverName.text} as uber mini fare.")
                        .setNegativeButton("Ok") { dialog, which ->
                        }
                        .show()

                    RouteTitle.text = "Completed"
                    var LatLng = LatLng(lat2,lon2)
                    myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(LatLng))!!
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng,18f))

                    val intent = Intent(applicationContext,PaymentReceipt::class.java)
                    startActivity(intent)
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.i("TTS_APP1", "Request has been modified")

                if(p0.key=="carPickupTime")
                {
                    VanPickupTime.text = p0.value.toString()
                }
                else if(p0.key=="carDropOffTime2")
                {
                    carDropOff.text = p0.value.toString()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.i("TTS_APP1", "Request has been deleted")
            }

        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.isZoomControlsEnabled = true


        val pakistan = LatLng(30.3753 ,  69.3451)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pakistan,18f))
        myMarker = mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)).position(pakistan))!!
    }


    fun handleCallDriverButton(v: View)
    {
        val intent =
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + DriverMobile.text.toString()))
        startActivity(intent)
    }

}