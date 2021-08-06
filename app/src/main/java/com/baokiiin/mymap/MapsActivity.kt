package com.baokiiin.mymap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.baokiiin.mymap.Utils.CODE
import com.baokiiin.mymap.Utils.defaultZoom
import com.baokiiin.mymap.Utils.diaLogBottom
import com.baokiiin.mymap.Utils.getData
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
    private var myMarker: Marker? = null
    private lateinit var markers: MutableList<Marker?>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var polyline: Polyline? = null
    private var locationPermissionGranted = false
    private var tmpMarker: Marker? = null
    private lateinit var locationAdapter: ItemLocationAdapter
    private var filter: Filter? = null
    private lateinit var data: MutableList<Market>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        clickView()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        clickMarker()
        data = getData()
        addMarkers(data)
        updateLocationUI()
        getDeviceLocation()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        // If request is cancelled, the result arrays are empty.
        when (requestCode) {
            CODE -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            }
        }
        updateLocationUI()
    }

    private fun clickMarker() {
        mMap.setOnMarkerClickListener {
            selectedMarker(it)
            true
        }
    }

    private fun selectedMarker(it: Marker) {
        tmpMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        polyline?.remove()
        it.showInfoWindow()
        if (it != myMarker) {
            it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            tmpMarker = it
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it.position,
                    filter?.zoom ?: defaultZoom
                )
            )
            locationAdapter.prevMarker = it.id
            locationAdapter.notifyDataSetChanged()
            val selectPosition = it.title?.substring(4)?.toInt()
            selectPosition?.let {
                binding.recycleView.scrollToPosition(
                    if (filter?.chan == 0) it else it / 2
                )
            }

        }

    }

    private fun setup() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationAdapter = ItemLocationAdapter {
            selectedMarker(it)
        }
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.recycleView.apply {
            adapter = locationAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun clickView() {
        binding.btnGetLocation.setOnClickListener {
            getDeviceLocation()
        }
        binding.btnFilter.setOnClickListener {
            diaLogBottom(
                this,
                layoutInflater,
                filter
            ) { filter ->
                callbackBottomSheet(filter)
            }.show()
        }
    }
    private fun callbackBottomSheet(filter:Filter){
        this.filter = filter
        val listMarker = data.filterIndexed { index, _ ->
            if (filter.chan == 1) index % 2 == 0 else if (filter.chan == 2) index % 2 != 0 else true
        }.toMutableList()
        val list = listMarker.subList(
            0,
            if (filter.count == 0) listMarker.size - 1 else if (filter.count == 1) 10 else 20
        )
        addMarkers(list)
        getDeviceLocation()
    }
    private fun drawTwoPosition(latLng: MutableList<LatLng>) {
        // vẽ nối 2 điểm
        val polylineOptions = PolylineOptions()
            .add(latLng[0])
            .add(latLng[1])
            .color(Color.RED)

        polyline = mMap.addPolyline(polylineOptions)
    }

    private fun markerOptions(market: Market): MarkerOptions {
        return MarkerOptions()
            .position(market.latLng)
            .title(market.title)
            .snippet(market.description)
    }

    private fun addMarkers(markets: MutableList<Market>) {
        mMap.clear()
        tmpMarker = null
        val tmpMarkets = mutableListOf<Marker?>()
        markets.forEach {
            tmpMarkets.add(mMap.addMarker(markerOptions(it)))
        }
        markers = tmpMarkets
        locationAdapter.submitList(markers)
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
                CODE
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
                    val myPosition = LatLng(it.latitude, it.longitude)
                    myMarker = mMap.addMarker(
                        markerOptions(Market("Me", "my location", myPosition))
                            .icon(bitmapDescriptorFromVector(R.drawable.ic_round_person))
                            .anchor(0.5f, 0.5f)
                    )
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            myPosition, filter?.zoom ?: defaultZoom
                        )
                    )
                    // vẽ vòng tròn
                    mMap.addCircle(drawCircle(myPosition))
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
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

    private fun drawCircle(myPosition: LatLng): CircleOptions {
        return CircleOptions()
            .center(myPosition)
            .radius(100.0) // In meters
            .strokeColor(Color.argb(100, 0, 188, 212))
            .fillColor(Color.argb(50, 0, 188, 212))
            .strokeWidth(5f)
            .clickable(true)
    }
}