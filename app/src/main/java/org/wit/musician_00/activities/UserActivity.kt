package org.wit.musician_00.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserLocation
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
            var userLocation = UserLocation(52.245696, -7.139102, 5f)
            if (user.userLocation.zoom != 0f) {
                userLocation.lat =  user.userLocation.lat
                userLocation.lng = user.userLocation.lng
                userLocation.zoom = user.userLocation.zoom
                userLocation = user.userLocation
            }
            i("userLocation: $userLocation")
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

        binding.userClipsDelete.setOnClickListener() {
            i("All clips delete clicked")
            val userClips = app.clips.findAll()
            val clipsToDelete = mutableListOf<ClipModel>()
            userClips.forEach { c ->
                if (c.userId == user.userId) {
                    clipsToDelete.add(c)
                }
            }
            app.clips.deleteAll(clipsToDelete)

            Snackbar.make(it,"All clips deleted, save changes to confirm", Snackbar.LENGTH_LONG).show()

        }

        binding.userBtnDelete.setOnClickListener() {
            val userClips = app.clips.findAll()
            val clipsToDelete = mutableListOf<ClipModel>()
            userClips.forEach { c ->
                if (c.userId == user.userId) {
                    clipsToDelete.add(c)
                }
            }
            app.clips.deleteAll(clipsToDelete)
            app.users.delete(user)
            setResult(RESULT_OK)

            val launcherIntent = Intent(this, LoginActivity::class.java)
            startActivity(launcherIntent)

            Snackbar.make(it,"Account and clips deleted", Snackbar.LENGTH_LONG).show()
            // finish()
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.user_cancel -> {
                finish()
                Toast.makeText(this, "Changes not Saved", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
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
                            val userLocation = result.data!!.extras?.getParcelable<UserLocation>("location")!!
                            i("Location == $userLocation")
                            user.lat = userLocation.lat
                            user.lng = userLocation.lng
                            user.zoom = userLocation.zoom
                            user.userLocation = UserLocation(userLocation.lat, userLocation.lng, userLocation.zoom)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}