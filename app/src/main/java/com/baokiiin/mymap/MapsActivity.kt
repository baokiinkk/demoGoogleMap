package com.baokiiin.mymap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baokiiin.mymap.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myPosition: LatLng
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var locationPermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            111 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val HCM1 = LatLng(10.762622, 106.660172)
        val HCM2 = LatLng(10.762622, 106.670172)

        updateLocationUI()
        getDeviceLocation()

        // vẽ nối 2 điểm
        val polylineOptions = PolylineOptions()
            .add(HCM1)
            .add(HCM2)
            .color(Color.RED)


        // create markers
        createMarket(mutableListOf(HCM1, HCM2))
        mMap.addPolyline(polylineOptions)
    }

    private fun createMarket(latLng: MutableList<LatLng>) {

        mMap.addMarker(
            MarkerOptions()
                .position(latLng[1])
                .title("Hà Nội")
                .flat(true)
        )

        mMap.addMarker(
            MarkerOptions()
                .position(latLng[0])
                .title("Hồ Chí Minh")
                .draggable(true)
        )
    }

    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                111
            )
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = mFusedLocationClient.lastLocation
                locationResult.addOnSuccessListener {
                    myPosition = LatLng(it.latitude, it.longitude)

                    mMap.addMarker(
                        MarkerOptions()
                            .position(myPosition)
                            .title("Me")
                            .icon(bitmapDescriptorFromVector(R.drawable.ic_round_person))
                            .anchor(0.5f,0.5f)
                    )
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            myPosition, 17f
                        )
                    )
                    // vẽ vòng tròn
                    val circleOptions = CircleOptions()
                        .center(myPosition)
                        .radius(30.0) // In meters
                        .strokeColor(Color.argb(100, 0, 188, 212))
                        .fillColor(Color.argb(50, 0, 188, 212))
                        .strokeWidth(5f)
                        .clickable(true)
                    mMap.addCircle(circleOptions)
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        vectorDrawable.draw(Canvas(bitmap))
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}