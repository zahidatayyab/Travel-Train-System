package com.app.tts

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ConfirmDetails : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var approxDistance : TextView
    private lateinit var approxTime : TextView
    private lateinit var confirmPickUpAddress : TextView
    private lateinit var confirmDropOffAddress : TextView

    private var  lat1 : Double = 0.0
    private var  lon1 : Double = 0.0
    private var lat2 : Double = 0.0
    private var  lon2 : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_details)

        approxDistance = findViewById(R.id.approxDistance)
        approxTime = findViewById(R.id.approxTime)
        confirmPickUpAddress = findViewById(R.id.confirmPickUpAddress)
        confirmDropOffAddress = findViewById(R.id.confirmDropOffAddress)

        val ext : Bundle? = intent.extras
        lat1 = ext?.getDouble("lat1")!!       // fetching the lat/lon passed from previous screens
        lon1 = ext.getDouble("lon1")
        lat2 = ext.getDouble("lat2")
        lon2 = ext.getDouble("lon2")

        //Toast.makeText(applicationContext,"$lat1 $lat2 $lon1 $lon2",Toast.LENGTH_SHORT).show()
        val origin : com.google.android.gms.maps.model.LatLng = com.google.android.gms.maps.model.LatLng(
            lat1, lon1
        )
        val destination : com.google.android.gms.maps.model.LatLng = com.google.android.gms.maps.model.LatLng(
            lat2, lon2
        )

        confirmPickUpAddress.text = getCompleteAddressString(lat1,lon1)
        confirmDropOffAddress.text = getCompleteAddressString(lat2,lon2)

        //shortest distance and its time calculator *******************         Note: Actual distance/time may vary
        val builder  = LatLngBounds.Builder()                                      //
        builder.include(origin)                                                    //
        builder.include(destination)                                               //    Setting the boundary of the map
        val bounds = builder.build()                                               //
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,50)   //

        val mapFragment = supportFragmentManager                                // setting maps
            .findFragmentById(R.id.mapConfirmDetails) as SupportMapFragment
        mapFragment.getMapAsync(this)


            //below initializing google directions
        val path: MutableList<List<com.google.android.gms.maps.model.LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin="+lat1+","+lon1+"&destination="+lat2+","+lon2+"&key=AIzaSyBPuIYB7MzH03waYZ1OTQ9RVNv2lX_A_U8"

       //getting the direction data in the form of json and then converting it
        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            val distance = legs.getJSONObject(0).getJSONObject("distance").getString("text")
            approxDistance.text=distance

            val time = legs.getJSONObject(0).getJSONObject("duration").getString("text")
            approxTime.text=time

            //drawing the lines against the route on the map
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                mMap.addPolyline(PolylineOptions().addAll(path[i]).color(getColor(R.color.colorRed)))
            }

            mMap.animateCamera(cameraUpdate)
        }, Response.ErrorListener {
                _ ->
        }){}

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.isZoomControlsEnabled = true

        val pakistan = com.google.android.gms.maps.model.LatLng(30.3753, 69.3451)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pakistan,10f))
    }

    fun handleConfirmDetailsBackButton(v: View)
    {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm")
            .setMessage("Are you sure you want to continue booking")
            .setNegativeButton("Yes") { dialog, which ->
                //Toast.makeText(applicationContext,"Done", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext,CompaniesSelection::class.java)
                intent.putExtra("kilometers",approxDistance.text)
                intent.putExtra("lon1",lon1)
                intent.putExtra("lat1",lat1)
                intent.putExtra("lon2",lon2)
                intent.putExtra("lat2",lat2)
                startActivity(intent)
            }
            .setPositiveButton("No") { dialog, which ->
                val intent = Intent(applicationContext,DashboardActivity::class.java)
                startActivity(intent)
            }
            .show()
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
                var country= addresses[0].countryName
                var city = addresses[0].locality

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
}