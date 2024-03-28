package com.getir.patika.foodcouriers

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : AppCompatActivity() {

//    private lateinit var tabLayout: TabLayout
//    private lateinit var viewPager2: ViewPager2
//    private lateinit var pagerAdapter: PagerAdapter
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var locationTextView: TextView
private lateinit var location_search: SearchView
private lateinit var mapFragment: MapFragment

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val fragment: Fragment = MapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.map, fragment).commit()

        // Request location permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location ->
                    mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as MapFragment?)!!
                    mapFragment?.updateMapLocation(location.latitude, location.longitude)
                    locationTextView= findViewById(R.id.locationTextView)
                    locationTextView.text= getAddress(location.latitude, location.longitude)
                    location_search= findViewById(R.id.location_search)
                    location_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let { searchAddress(it) }
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return false
                        }
                    })
                }
                .addOnFailureListener { exception ->
                }

        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location ->
                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
                        mapFragment?.updateMapLocation(location.latitude, location.longitude)

                    }
                    .addOnFailureListener { exception ->
                        //TODO Handle failure
                    }

            } else {
                //TODO Permission denied, handle accordingly
            }
        }
    }

    private fun getAddress(lat: Double, longitude: Double): String?{
        val geocoder= Geocoder(this)
        val list= geocoder.getFromLocation(lat, longitude, 1)
        return list?.get(0)?.getAddressLine(0)
    }

    private fun searchAddress(query: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(query, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latitude = address.latitude
                val longitude = address.longitude

                // Update map with searched location
                mapFragment.updateMapLocation(latitude, longitude)

                // Update TextView with searched address
                locationTextView.text = address.getAddressLine(0)
            } else {
                locationTextView.text = "Address not found"
            }
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }


}