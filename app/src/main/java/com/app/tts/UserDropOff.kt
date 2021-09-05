package com.app.tts

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

class UserDropOff : AppCompatActivity() , OnMapReadyCallback {
    private var placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
    private lateinit var mMap: GoogleMap
    private lateinit var myMarker : Marker
    private var lat2 : Double = 0.0
    private var lon2 : Double = 0.0
    private var country : String = ""
    private var city : String = ""

    private  lateinit var userDropOffAddressLabel : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_drop_off)

        Places.initialize(applicationContext, getString(R.string.Places_Api))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDropOff) as SupportMapFragment
        mapFragment.getMapAsync(this)

        userDropOffAddressLabel = findViewById(R.id.userDropOffAddressDisplay)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.isZoomControlsEnabled = true


        val pakistan = LatLng(30.3753 ,  69.3451)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pakistan))
        myMarker = mMap.addMarker(MarkerOptions().position(pakistan))!!
    }

    fun handleDropOffBackButton(v: View)
    {
        val intent = Intent(applicationContext,DashboardActivity::class.java)
        startActivity(intent)
    }

    fun handleDropOffAddTextField(v:View)
    {
        val intent : Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,placeFields)
            .build(this)
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1 && resultCode== RESULT_OK)
        {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            getCompleteAddressString(place.latLng!!.latitude, place.latLng!!.longitude)
            userDropOffAddressLabel.setText(place.name)

            var latLng = place.latLng
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!,18f))
            myMarker.remove()

            myMarker= mMap.addMarker(MarkerOptions().position(latLng).draggable(true))!!

            mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener
            {
                override fun onMarkerDragEnd(p0: Marker?) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(p0!!.position))

                    val add = getCompleteAddressString(p0.position.latitude, p0.position.longitude)
                    userDropOffAddressLabel.setText(add)
                    latLng=p0.position
                    lat2 = latLng!!.latitude
                    lon2 = latLng!!.longitude
                }

                override fun onMarkerDragStart(p0: Marker?) {
                    Log.d("System out", "onMarkerDragStart..."+p0!!.position.latitude+"..."+p0.position.longitude)
                }

                override fun onMarkerDrag(p0: Marker?) {
                    Log.i("System out", "onMarkerDrag...")
                }

            })

            lat2 = latLng!!.latitude
            lon2 = latLng!!.longitude
        }

        else if (resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Toast.makeText(applicationContext,"An Error Occurred. Try Again", Toast.LENGTH_SHORT).show()

        }

        else
        {
            lat2 = 0.0
            lon2 = 0.0
            Toast.makeText(applicationContext,"Please select the location to continue.", Toast.LENGTH_SHORT).show()
            mMap.clear()

        }
    }

    private fun getCompleteAddressString(
        LATITUDE: Double,
        LONGITUDE: Double
    ) : String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                country= addresses[0].countryName
                city=addresses[0].locality
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

    fun handleUserDropOffContinueButton(v:View) {
        val ext: Bundle? = intent.extras
        val lat1 = ext?.getDouble("lat1")
        val lon1 = ext?.getDouble("lon1")

        if (lat2 == 0.0 && lon2 == 0.0) // nothing selected
        {
            Toast.makeText(
                applicationContext,
                "Please Select the pick up location",
                Toast.LENGTH_SHORT
            ).show()
        } else if (country != "Pakistan") // checking if location out of Pakistan selected
        {
            if (lat2 in 24.0..37.0 && lon2 in 61.0..79.0) {
                val intent = Intent(baseContext,ConfirmDetails::class.java)
                intent.putExtra("lat1",lat1)
                intent.putExtra("lon1",lon1)
                intent.putExtra("lat2",lat2)
                intent.putExtra("lon2",lon2)
                startActivity(intent)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please select a city from Pakistan to continue.",
                    Toast.LENGTH_SHORT
                ).show()
                userDropOffAddressLabel.setText(country)
            }
        }
        else
        {
            val intent = Intent(baseContext,ConfirmDetails::class.java)
            intent.putExtra("lat1",lat1)
            intent.putExtra("lon1",lon1)
            intent.putExtra("lat2",lat2)
            intent.putExtra("lon2",lon2)
            startActivity(intent)
        }
    }

}