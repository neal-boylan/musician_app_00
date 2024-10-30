package org.wit.musician_00.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
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
import org.wit.musician_00.models.UserLocation
import org.wit.musician_00.models.UserModel
import timber.log.Timber.i

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>

    var user = UserModel()
    lateinit var app : MainApp
    // var userLocation = UserLocation(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        user = intent.extras?.getParcelable("user_details")!!

        binding.toolbarAdd.title = "User Details"
        binding.editUsername.setText(user.email)
        // userLocation = user.userLocation

        setSupportActionBar(binding.toolbarAdd)

        binding.chooseUserImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.addUserLocation.setOnClickListener {
            val userLocation = UserLocation(52.245696, -7.139102, 5f)
            if (user.zoom != 0f) {
                userLocation.lat =  user.lat
                userLocation.lng = user.lng
                userLocation.zoom = user.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java).putExtra("userLocation", userLocation)
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.userBtnAdd.setOnClickListener() {
            user.email = binding.editUsername.text.toString()
            user.password = binding.editPassword.text.toString()

            if (user.email.isNotEmpty()) {
                app.users.update(user.copy())
                val clips = app.clips.findAll()
                clips.forEach { clip ->
                    if (clip.userId == user.userId) {
                        clip.image = user.userImage
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
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            user.userImage = result.data!!.data!!
                            Picasso.get().load(user.userImage).into(binding.userImage)
                            binding.chooseUserImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { }
                    else -> { }
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
                            val userLocation = result.data!!.extras?.getParcelable<UserLocation>("userLocation")!!
                            i("Location == $userLocation")
                            user.lat = userLocation.lat
                            user.lng = userLocation.lng
                            user.zoom = userLocation.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}