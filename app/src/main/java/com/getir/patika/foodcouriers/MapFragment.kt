package com.getir.patika.foodcouriers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(),OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private var circle1: Circle?= null
    private var circle2:Circle?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialLocation= LatLng(0.0,0.0)
        val circleOptions= CircleOptions()
            .center(initialLocation)
            .radius(100.0)
        circle1= mMap.addCircle(circleOptions)
        circle2=mMap.addCircle(circleOptions)
        marker= mMap.addMarker(MarkerOptions()
            .icon(this.context?.let { bitmapFromVector(it, R.drawable.placeholder) })
            .position(initialLocation))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation))
    }
    @SuppressLint("MissingPermission")
    fun updateMapLocation(latitude: Double, longitude: Double) {
        marker?.remove()
        circle1?.remove()
        circle2?.remove()

        val location = LatLng(latitude, longitude)
        marker = mMap.addMarker(MarkerOptions()
            .icon(this.context?.let { bitmapFromVector(it, R.drawable.placeholder) })
            .position(location))
        val circleOptions1 = CircleOptions()
            .center(location)
            .radius(50.0)
            .strokeWidth(0f) // Adjust the stroke width as needed
            .fillColor(Color.argb((0.21 * 255).toInt(), 214, 19, 85)) // Set the fill color to transparent

        val circleOptions2 = CircleOptions()
            .center(location)
            .radius(100.0)
            .strokeWidth(0f) // Adjust the stroke width as needed
            .fillColor(Color.argb((0.21 * 255).toInt(), 214, 19, 85)) // Set the fill color to transparent

        circle1= mMap.addCircle(circleOptions1)
        circle2= mMap.addCircle(circleOptions2)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
    }

    private fun bitmapFromVector(context: Context, vectorResId:Int): BitmapDescriptor? {
        //drawable generator
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context,vectorResId)!!
        vectorDrawable.setBounds(0,0,vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)
        //bitmap genarator
        val bitmap: Bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        //pass bitmap in canvas constructor
        val canvas = Canvas(bitmap)
        //pass canvas in drawable
        vectorDrawable.draw(canvas)
        //return BitmapDescriptorFactory
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}