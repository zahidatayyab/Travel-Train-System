package com.app.tts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.app.tts.databinding.ActivityDashboardBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var auth: FirebaseAuth // firebase authenticator
    private lateinit var reference : DatabaseReference  // firebase database

    private lateinit var toggle: ActionBarDrawerToggle  // used for toolbar
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mMap: GoogleMap    //  maps

    private val PERMISSION_ID = 42      // permission id used for using location etc
    private lateinit var mFusedLocationClient: FusedLocationProviderClient  // used for getting current location
    private lateinit var current : LatLng   /// storing current location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth    // setting the authenticator
        reference = Firebase.database.reference.child("Users").child(auth.currentUser!!.uid)    // getting the user tree path

        reference.addListenerForSingleValueEvent(object : ValueEventListener {  // fetching user details
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(baseContext,p0.message,Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(p0: DataSnapshot) {

                //Toast.makeText(applicationContext,p0.child("name").value.toString(),Toast.LENGTH_SHORT).show()
                //drawerName.text = p0.child("name").value.toString()
                //drawerPhone.text = p0.child("phone").value.toString()
                //drawerRating.text = p0.child("rating").value.toString()
            }
        })


        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager                            //*
            .findFragmentById(R.id.homeScreenMap) as SupportMapFragment     //* Setting map
        mapFragment.getMapAsync(this)                               //*

        val toolbar: Toolbar = findViewById(R.id.toolbar)       //2
        setSupportActionBar(toolbar)                            //2

        val drawerLayout: DrawerLayout = binding.drawerLayout   //2
        val navView: NavigationView = binding.navView           //2

        toggle = ActionBarDrawerToggle(                         //2     Setting the navigation or tool bar
            this,
            drawerLayout,                                       //2
            toolbar,                                            //2
            R.string.navigation_drawer_open,                           //2
            R.string.navigation_drawer_close                           //2
        )                                                       //2
        drawerLayout.addDrawerListener(toggle)                  //2



        //below is the drawer item selected listner
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(applicationContext, DashboardActivity::class.java)
                    startActivity((intent))
                    finish()
                //Toast.makeText(applicationContext, "Home", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_settings -> {
                    val intent = Intent(applicationContext, UserSettings::class.java)
                    startActivity(intent)
                }

                R.id.nav_logOut -> {

                    val user = auth.currentUser
                    if (user != null) {
                        auth.signOut()
                    }
                    val intent = Intent(applicationContext, UserLogin::class.java)
                    startActivity((intent))
                    finish()
                }

                R.id.nav_yourRide -> {
                    val intent = Intent(applicationContext, History::class.java)
                    startActivity((intent))
                }

                R.id.nav_notifications -> {
                    Toast.makeText(applicationContext, "Notifications", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_myWallet -> {
                    val intent = Intent(applicationContext, Wallet::class.java)
                    startActivity((intent))
                }
            }
            true
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)    //3
        getLastLocation()                                                               //3 Getting current location


    }

    override fun onMapReady(googleMap: GoogleMap) { //setting maps
        mMap = googleMap

        mMap.setMapStyle(   // changing maps default white color
            MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        mMap.uiSettings.isZoomControlsEnabled = true

        val pakistan = LatLng(30.3753 ,  69.3451)   // showing pakistan as default
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pakistan))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {    // sincing the navigation bar
        super.onPostCreate(savedInstanceState)
            toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {   // navigation drawer on option selected
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {     // getting last location
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else
                    {
                        mMap.isMyLocationEnabled= true

                        current = LatLng(location.latitude, location.longitude)
                        //mMap.addMarker(MarkerOptions().position(current).title("You are here"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18f))
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation

            current = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18f))


        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {   // checking for permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    fun OnSelectPickUp(view: View)
    {
        val intent = Intent(baseContext, UserPickUp::class.java)
        intent.putExtra("clat",current.latitude)
        intent.putExtra("clon",current.longitude)
        startActivity((intent))
    }
}