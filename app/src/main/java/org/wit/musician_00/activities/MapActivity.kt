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
import org.wit.musician_00.models.Location

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        location = intent.extras?.getParcelable<Location>("location")!!
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        onBackPressedDispatcher.addCallback(this ) {
            if (!intent.hasExtra("clip_edit")) {
                val resultIntent = Intent()
                resultIntent.putExtra("location", location)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val userLoc = LatLng(location.lat, location.lng)
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, location.zoom))
    }

    override fun onMarkerDrag(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        val loc = LatLng(location.lat, location.lng)
        marker.snippet = "GPS : $loc"
    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
    }

    override fun onMarkerDragStart(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        val loc = LatLng(location.lat, location.lng)
        marker.snippet = "GPS : $loc"
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        val loc = LatLng(location.lat, location.lng)
        marker.snippet = "GPS : $loc"
        return false
    }

    override fun onCameraMove() {
        location.zoom = map.cameraPosition.zoom
    }
}