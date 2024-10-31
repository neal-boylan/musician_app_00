package org.wit.musician_00.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityMapBinding
import org.wit.musician_00.models.UserLocation
import androidx.activity.addCallback

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private var userLocation = UserLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userLocation = intent.extras?.getParcelable<UserLocation>("userLocation")!!
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        onBackPressedDispatcher.addCallback(this ) {
            val resultIntent = Intent()
            resultIntent.putExtra("userLocation", userLocation)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val userLoc = LatLng(userLocation.lat, userLocation.lng)
        val options = MarkerOptions()
            .title("User")
            .snippet("GPS : $userLoc")
            .draggable(true)
            .position(userLoc)
        map.addMarker(options)?.showInfoWindow()
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMarkerDragListener(this)
        map.setOnCameraMoveListener(this)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, userLocation.zoom))
    }

    override fun onMarkerDrag(marker: Marker) {
        userLocation.lat = marker.position.latitude
        userLocation.lng = marker.position.longitude
        userLocation.zoom = map.cameraPosition.zoom
        val loc = LatLng(userLocation.lat, userLocation.lng)
        marker.snippet = "GPS : $loc"
    }

    override fun onMarkerDragEnd(marker: Marker) {
        userLocation.lat = marker.position.latitude
        userLocation.lng = marker.position.longitude
        userLocation.zoom = map.cameraPosition.zoom
    }

    override fun onMarkerDragStart(marker: Marker) {
        userLocation.lat = marker.position.latitude
        userLocation.lng = marker.position.longitude
        userLocation.zoom = map.cameraPosition.zoom
        val loc = LatLng(userLocation.lat, userLocation.lng)
        marker.snippet = "GPS : $loc"
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        userLocation.lat = marker.position.latitude
        userLocation.lng = marker.position.longitude
        userLocation.zoom = map.cameraPosition.zoom
        val loc = LatLng(userLocation.lat, userLocation.lng)
        marker.snippet = "GPS : $loc"
        return false
    }

    override fun onCameraMove() {
        userLocation.zoom = map.cameraPosition.zoom
    }
}