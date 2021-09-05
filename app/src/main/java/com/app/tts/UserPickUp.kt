package com.app.tts

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


class UserPickUp : AppCompatActivity() , OnMapReadyCallback {
    private var placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
    private lateinit var mMap: GoogleMap    // google map
    private lateinit var myMarker : Marker  // the red colored marker used

    private var lat1 : Double = 0.0     // used for storing pickup latitude
    private var lon1 : Double = 0.0     // used for storing pickup longitude
    private var country : String = ""   // for storing country
    private var city : String = ""      // for storing city

    private lateinit var pickUpAddressDisplay : TextView    // used for fetching textview pickup address data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pick_up)

        Places.initialize(applicationContext, getString(R.string.Places_Api))   // initializing places used for searching place
        pickUpAddressDisplay=findViewById(R.id.pickUpAddressDisplay)

        val mapFragment = supportFragmentManager    // initializing maps
            .findFragmentById(R.id.mapPickUp) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    fun handlePickUpLocationTextField(v: View)
    {
        val intent : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,placeFields)
            .build(this)    // function provided by google documentation for getting the place
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1 && resultCode== RESULT_OK)    // if place is found
        {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            pickUpAddressDisplay.setText(place.name)        // setting the address to the tet field
            getCompleteAddressString(place.latLng!!.latitude, place.latLng!!.longitude)

            var latLng = place.latLng

            val location = CameraUpdateFactory.newLatLngZoom(   // moving . updating the maps to new searched place
                latLng!!, 18f
            )
            mMap.animateCamera(location)
            myMarker.remove()

            myMarker= mMap.addMarker(MarkerOptions().position(latLng).draggable(true))!!    // making  marker moveable

            mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener // if marker is dragged then
            {
                override fun onMarkerDragEnd(p0: Marker?) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(p0!!.position))

                    val add = getCompleteAddressString(p0.position.latitude, p0.position.longitude) // getting address against lat/lon
                    pickUpAddressDisplay.setText(add)
                    latLng=p0.position
                    lat1 = latLng!!.latitude        // saving new marker dragged position
                    lon1 = latLng!!.longitude
                }

                override fun onMarkerDragStart(p0: Marker?) {
                    Log.d("System out", "onMarkerDragStart..."+p0!!.position.latitude+"..."+p0.position.longitude)
                }

                override fun onMarkerDrag(p0: Marker?) {
                    Log.i("System out", "onMarkerDrag...")
                }

            })

            lat1 = latLng!!.latitude
            lon1 = latLng!!.longitude
        }

        else if (resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Toast.makeText(applicationContext,"An Error Occurred. Try Again", Toast.LENGTH_SHORT).show()
        }

        else
        {
            Toast.makeText(applicationContext,"Please select the location to continue.", Toast.LENGTH_SHORT).show()

            lat1 = 0.0
            lon1 = 0.0

            mMap.clear()
        }

    }

    fun handleBackButton(v: View)
    {
        val intent = Intent(baseContext, DashboardActivity::class.java)
        startActivity((intent))
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val ext : Bundle? = intent.extras
        lat1 = ext?.getDouble("clat")!!    // fetching the lat/lon passed from previous screens
        lon1 = ext.getDouble("clon")


        pickUpAddressDisplay.setText(getCompleteAddressString(lat1, lon1))        // setting the address to the tet field


        mMap = googleMap

        mMap.setMapStyle(   // changing maps color
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.isZoomControlsEnabled = true

        var latLng = LatLng(lat1,lon1)

        val location = CameraUpdateFactory.newLatLngZoom(   // moving . updating the maps to new searched place
            latLng, 18f
        )
        mMap.animateCamera(location)

        myMarker= mMap.addMarker(MarkerOptions().position(latLng).draggable(true))!!    // making  marker moveable

        mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener // if marker is dragged then
        {
            override fun onMarkerDragEnd(p0: Marker?) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(p0!!.position))

                val add = getCompleteAddressString(p0.position.latitude, p0.position.longitude) // getting address against lat/lon
                pickUpAddressDisplay.setText(add)
                latLng=p0.position
                lat1 = latLng.latitude        // saving new marker dragged position
                lon1 = latLng.longitude
            }

            override fun onMarkerDragStart(p0: Marker?) {
                Log.d("System out", "onMarkerDragStart..."+p0!!.position.latitude+"..."+p0.position.longitude)
            }

            override fun onMarkerDrag(p0: Marker?) {
                Log.i("System out", "onMarkerDrag...")
            }

        })

        lat1 = latLng.latitude
        lon1 = latLng.longitude
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

    fun handleUserPickUpContinueButton(v:View)
    {
        if(lat1==0.0 && lon1 == 0.0) //checking if nothing selected
        {
            Toast.makeText(applicationContext,"Please Select Deliver to location", Toast.LENGTH_SHORT).show()
        }

        else if(country!="Pakistan") // checking if location out of Pakistan selected
        {
            if(lat1 in 24.0..37.0 && lon1 in 61.0..79.0)
            {
                val intent = Intent(baseContext,UserDropOff::class.java)    // moving to next screen
                intent.putExtra("lat1",lat1)    // passing lat/lon of pickup to next screen
                intent.putExtra("lon1",lon1)    //
                startActivity(intent)
            }

            else
            {
                Toast.makeText(applicationContext,"Please select a city from Pakistan to continue.",
                    Toast.LENGTH_SHORT).show()
                pickUpAddressDisplay.setText(country)
            }
        }

        else    // the best case
        {
            val intent = Intent(baseContext,UserDropOff::class.java)
            intent.putExtra("lat1",lat1)
            intent.putExtra("lon1",lon1)
            startActivity(intent)
        }
    }
}