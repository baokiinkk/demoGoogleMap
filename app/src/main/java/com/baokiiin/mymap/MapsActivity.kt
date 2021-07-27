package com.baokiiin.mymap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myPosition: LatLng
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var polyline:Polyline? = null
    private var locationPermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        clickView()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        clickMarket()
        updateLocationUI()
        getDeviceLocation()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(market: Market) {
        mMap.addMarker(marketOptions(market))?.showInfoWindow()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                market.latLng, 17f
            )
        )
    }
    private fun clickMarket(){
        mMap.setOnMarkerClickListener {
            polyline?.remove()
            val market = Market(it.title,it.snippet,it.position)
            Utils.diaLogBottom(this,layoutInflater,market){
                drawTwoPosition(mutableListOf(myPosition,market.latLng))
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        myPosition, 16f
                    )
                )
            }.show()
            true
        }
    }
    private fun setup() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        EventBus.getDefault().register(this)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun clickView() {
        binding.btnGetLocation.setOnClickListener {
            getDeviceLocation()
        }
        binding.btnShowListLocation.setOnClickListener {
            Utils.showDialog(this)
        }
    }

    private fun drawTwoPosition(latLng: MutableList<LatLng>) {
        // vẽ nối 2 điểm
        val polylineOptions = PolylineOptions()
            .add(latLng[0])
            .add(latLng[1])
            .color(Color.RED)

        polyline = mMap.addPolyline(polylineOptions)
    }

    private fun marketOptions(market: Market): MarkerOptions {
        return MarkerOptions()
            .position(market.latLng)
            .title(market.title)
            .snippet(market.description)
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
                        marketOptions(Market("Me", "my location", myPosition))
                            .icon(bitmapDescriptorFromVector(R.drawable.ic_round_person))
                            .anchor(0.5f, 0.5f)
                    )?.showInfoWindow()
                    mMap.animateCamera(
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