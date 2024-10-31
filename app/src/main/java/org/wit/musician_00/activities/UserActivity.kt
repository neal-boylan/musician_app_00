package org.wit.musician_00.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityUserBinding
import org.wit.musician_00.helpers.showImagePicker
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.Location
import org.wit.musician_00.models.UserModel
import timber.log.Timber.i

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>

    var user = UserModel()
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        user = intent.extras?.getParcelable("user_details")!!

        binding.toolbarAdd.title = "User Details"
        binding.editUsername.setText(user.email)
        binding.editPassword.setText(user.password)
        Picasso.get().load(user.userImage).into(binding.userImage)
        // userLocation = user.userLocation

        setSupportActionBar(binding.toolbarAdd)

        binding.chooseUserImage.setOnClickListener {
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
            imageIntentLauncher.launch(request)
        }

        binding.addUserLocation.setOnClickListener {
            var userLocation = Location(52.245696, -7.139102, 5f)
            if (user.userLocation.zoom != 0f) {
                userLocation.lat =  user.userLocation.lat
                userLocation.lng = user.userLocation.lng
                userLocation.zoom = user.userLocation.zoom
                userLocation = user.userLocation
            }
            val launcherIntent = Intent(this, MapActivity::class.java).putExtra("location", userLocation)
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.userBtnAdd.setOnClickListener() {
            user.email = binding.editUsername.text.toString()
            user.password = binding.editPassword.text.toString()

            if (user.email.isNotEmpty()) {
                app.users.update(user.copy())
                val clips = app.clips.findAll()

                clips.forEach { c ->
                    if (c.userId == user.userId) {
                        c.image = user.userImage
                        c.lat = user.lat
                        c.lng = user.lng
                        c.zoom = user.zoom
                        app.clips.update(c.copy())
                    }
                }
                setResult(RESULT_OK)
                val launcherIntent =
                    Intent(this, ClipListActivity::class.java).putExtra(
                        "user_details",
                        user
                    )
                startActivity(launcherIntent)
                // finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            try{
                contentResolver.takePersistableUriPermission(it!!, Intent.FLAG_GRANT_READ_URI_PERMISSION )
                user.userImage = it // The returned Uri
                i("IMG :: ${user.userImage}")
                Picasso.get().load(user.userImage).into(binding.userImage)
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got User Location ${result.data.toString()}")
                            //location = result.data!!.extras?.getParcelable("location",Location::class.java)!!
                            val userLocation = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $userLocation")
                            user.lat = userLocation.lat
                            user.lng = userLocation.lng
                            user.zoom = userLocation.zoom
                            user.userLocation = Location(userLocation.lat, userLocation.lng, userLocation.zoom)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}