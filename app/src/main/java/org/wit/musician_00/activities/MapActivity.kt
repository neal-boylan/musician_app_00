package org.wit.musician_00.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityMapBinding
// import org.wit.musician_00.models.Location
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserLocation
import timber.log.Timber.i

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private var location = UserLocation()
    private var clip = ClipModel()
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        location = intent.extras?.getParcelable<UserLocation>("location")!!
 //       clip = intent.extras?.getParcelable<ClipModel>("clip_edit")!!
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        getCurrentLocationUser()

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

    private fun getCurrentLocationUser() {
        if(ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }

        val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            userLocation ->

            if(userLocation != null){
                if(intent.hasExtra("clip_edit")) {
                    location = intent.extras?.getParcelable<UserLocation>("location")!!
                    val tempLocation: Location = userLocation
                    tempLocation.latitude = location.lat
                    tempLocation.longitude = location.lng
                    currentLocation = tempLocation
                } else {
                    currentLocation = userLocation
                }
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()

                val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionCode -> if(grantResults.isNotEmpty() && grantResults[0]==
                PackageManager.PERMISSION_GRANTED){
                getCurrentLocationUser()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
//        val userLoc = LatLng(location.lat, location.lng)
//        val options = MarkerOptions()
//            .title("User")
//            .snippet("GPS : $userLoc")
//            .draggable(true)
//            .position(userLoc)
//        map.addMarker(options)?.showInfoWindow()
//        map.uiSettings.isZoomControlsEnabled = true
//        map.uiSettings.isZoomGesturesEnabled = true
//        map.setOnMarkerClickListener(this)
//        map.setOnMarkerDragListener(this)
//        map.setOnCameraMoveListener(this)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, location.zoom))

        val latlng = LatLng(currentLocation.latitude, currentLocation.longitude)
        var markerOptions = MarkerOptions()
            .position(latlng)
            .title("Location")
            .snippet("GPS : $latlng")
            .draggable(true)
            .position(latlng)
//        if(intent.hasExtra("clip_edit")){
//            markerOptions= MarkerOptions()
//                .position(latlng)
//                .title(clip.title)
//                .snippet("GPS : $latlng")
//                .draggable(true)
//                .position(latlng)
//        } else{
//            markerOptions= MarkerOptions()
//                .position(latlng)
//                .title("Current Location")
//                .snippet("GPS : $latlng")
//                .draggable(true)
//                .position(latlng)
//        }
        map.addMarker(markerOptions)?.showInfoWindow()
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMarkerDragListener(this)
        map.setOnCameraMoveListener(this)
//        map.animateCamera(CameraUpdateFactory.newLatLng(latlng))
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 7f))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 7f))


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