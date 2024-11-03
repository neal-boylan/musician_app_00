package org.wit.musician_00.activities

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityClipBinding
import org.wit.musician_00.helpers.showAudioPicker
import org.wit.musician_00.helpers.showImagePicker
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserLocation
import org.wit.musician_00.models.UserModel
import timber.log.Timber.i
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.Random


class ClipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClipBinding
    private lateinit var audioIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var app : MainApp

    private var clip = ClipModel()
    private var user = UserModel()
    private var location = UserLocation(52.245696, -7.139102, 5f)
    private var edit = false
    private var userClip = false
    private val instrumentList = arrayListOf("Guitar", "Bass", "Drums", "Other")
    private var spinnerPosition: Int = instrumentList.size
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        i("Clip Activity started..")
        user = intent.extras?.getParcelable("user_details")!!
        mediaPlayer = MediaPlayer.create(this, R.raw.new_recording_7)

        binding.playBtn.isVisible = false
        binding.stopBtn.isVisible = false
        binding.pauseBtn.isVisible = false

        if (intent.hasExtra("clip_edit")) {
            clip = intent.extras?.getParcelable("clip_edit")!!
            if (clip.audio != Uri.EMPTY) {
                binding.playBtn.isVisible = true
                binding.stopBtn.isVisible = true
                binding.pauseBtn.isVisible = true
            }
        }

        binding.clipLocation.isVisible = false


        // Spinner tutorial: https://www.youtube.com/watch?v=_WCRrD9_ffE&t=527s
        binding.instrumentSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, instrumentList)
        binding.instrumentSpinner.onItemSelectedListener = object : OnItemSelectedListener,
            AdapterView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                return true
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                i("instrumentList[position]: ${instrumentList[position]}")
                if (instrumentList[position] == "Other"){
                    binding.clipInstrument.isVisible = true
                }else{
                    binding.clipInstrument.isVisible = false
                }
                if (instrumentList.contains(clip.instrument)){
                    binding.clipInstrument.setText("")
                }
                clip.instrument = instrumentList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        if (intent.hasExtra("clip_edit")) {
            clip = intent.extras?.getParcelable("clip_edit")!!
            binding.clipInstrument.setText(clip.instrument)

            if (clip.userId != user.userId) {
                binding.instrumentSpinner.isVisible = false
                binding.clipInstrument.isEnabled = false
            } else {
                if (instrumentList.contains(clip.instrument)){
                    spinnerPosition = instrumentList.indexOf(clip.instrument)
                }else {
                    spinnerPosition = instrumentList.size - 1
                }
                binding.instrumentSpinner.setSelection(spinnerPosition)
            }
        }



        var chipId : Int = 0
        // chip group tutorial https://www.youtube.com/watch?v=lU6YyPQWvgY
        var genreList = arrayListOf("Rock", "Metal", "Alternative", "Pop", "Jazz", "Country", "Rap", "Blues", "Funk", "Soul", "Other")

        if (intent.hasExtra("clip_edit")) {
            clip = intent.extras?.getParcelable("clip_edit")!!
            if (clip.userId != user.userId) {
                genreList.clear()
                genreList = clip.genres
            }
        }

        genreList.forEach { genre ->
            val chip = LayoutInflater.from(this).inflate(R.layout.chip_layout, binding.chipGroup, false) as Chip
            chip.id = chipId
            chipId++
            chip.text = genre
            chip.isCheckable = true
            if (intent.hasExtra("clip_edit")) {
                if (clip.userId != user.userId) {
                    chip.isCheckable = false
                }
            }
            binding.chipGroup.addView(chip)
        }

        if (intent.hasExtra("clip_edit")) {
            clip = intent.extras?.getParcelable("clip_edit")!!
            edit = true
            binding.clipTitle.setText(clip.title)
            binding.clipDescription.setText(clip.description)
            if (clip.userId == user.userId) {
                userClip = true
                binding.clipTitle.isEnabled = true
                binding.clipTitle.background = null
                binding.clipDescription.isEnabled = true
                binding.btnAdd.isVisible = true
                binding.btnAdd.text = getString(R.string.button_saveClip)
            } else {
                binding.btnAdd.isVisible = false
                binding.chooseAudio.isVisible = false
                binding.clipLocation.isVisible = true
                binding.clipLocation.text = "View Location"
            }
            val clipUser:UserModel? = app.users.findByUserId(clip.userId)
            binding.toolbarAdd.title = "${clip.title} by ${clipUser?.email}"
            Picasso.get().load(clip.image).into(binding.clipImage)
            if (clip.audio != Uri.EMPTY) {
                binding.chooseAudio.text = "Change Audio"
            }
            location = clip.location
            binding.chipGroup.forEach { item ->
                if (clip.genres.contains(genreList[item.id])){
                binding.chipGroup.check(item.id)}
            }
        } else {
            binding.toolbarAdd.title = "Add New Clip"
            binding.clipTitle.isEnabled = true
            binding.clipDescription.isEnabled = true
            binding.btnAdd.isVisible = true
            binding.clipImage.isVisible = false
        }

        setSupportActionBar(binding.toolbarAdd)

        val checkedGenres: ArrayList<String>
        if (intent.hasExtra("clip_edit")) {
            if (clip.userId == user.userId) {
                checkedGenres = clip.genres
            } else {
                checkedGenres = arrayListOf<String>()
            }
        } else {
            checkedGenres = arrayListOf<String>()
        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()){
                i("No Genres")
            } else{
                checkedGenres.clear()
                checkedIds.forEach { idx ->
                    val chip = findViewById<Chip>(idx)
                    checkedGenres.add(chip.text.toString())
                    // clip.genres.add(chip.text.toString())
                }
            }
        }


        binding.chooseAudio.setOnClickListener {
            showAudioPicker(audioIntentLauncher)
        }

        binding.stopBtn.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.prepare()
            }
        }
        binding.playBtn.setOnClickListener {
            mediaPlayer.start()
        }

        binding.pauseBtn.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        binding.clipLocation.setOnClickListener {
            val location = UserLocation(52.245696, -7.139102, 15f)
            if (clip.zoom != 0f) {
                location.lat =  clip.lat
                location.lng = clip.lng
                location.zoom = clip.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
            launcherIntent.putExtra("location", location)
            if (intent.hasExtra("clip_edit")) {
            launcherIntent.putExtra("clip_edit", clip)}
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.btnAdd.setOnClickListener() {
            clip.title = binding.clipTitle.text.toString()
            clip.description = binding.clipDescription.text.toString()
            clip.genres = checkedGenres

            clip.image = user.userImage
            clip.lat = user.lat
            clip.lng = user.lng
            clip.zoom = user.zoom

//            if (clip.title.isNotEmpty()) {
//                if (binding.instrumentSpinner.selectedItem.toString() == "Other" && binding.clipInstrument.text.isEmpty()) {
//                    Snackbar.make(it, "Please specify an Instrument", Snackbar.LENGTH_LONG).show()
//                } else {
//                    if (binding.instrumentSpinner.selectedItem.toString() == "Other") {
//                        i("Other, binding.clipInstrument.text.toString(): ${binding.clipInstrument.text}")
//                        clip.instrument = binding.clipInstrument.text.toString()
//                    }
//                    if (edit) {
//                        clip.userId = user.userId
//                        clip.clipEditDate = "Last Edited: ${LocalDate.now()}"
//                        app.clips.update(clip.copy())
//                    } else {
//                        clip.userId = user.userId
//                        clip.clipDate = "Date Added: ${LocalDate.now()}"
//                        app.clips.create(clip.copy())
//                    }
//                    setResult(RESULT_OK)
//                    finish()
//                }
//            } else {
//                Snackbar.make(it, "Please Enter a title", Snackbar.LENGTH_LONG).show()
//            }

            if (clip.title.isNotEmpty()) {
                if (binding.instrumentSpinner.selectedItem.toString() == "Other") {
                    if (binding.clipInstrument.text.isEmpty()) {
                        Snackbar.make(it, "Please specify an Instrument", Snackbar.LENGTH_LONG).show()
                    } else {
                        clip.instrument = binding.clipInstrument.text.toString()
                    }
                }

                if (clip.instrument == "Other"){
                    i("It's OTHER!")
                    clip.instrument = binding.clipInstrument.text.toString()
                }
                if (edit) {
                    i("edit clip.instrument: ${clip.instrument}")
                    clip.userId = user.userId
                    clip.clipEditDate = "Last Edited: ${LocalDate.now()}"
                    app.clips.update(clip.copy())
                } else {
                    i("new clip.instrument: ${clip.instrument}")
                    clip.userId = user.userId
                    clip.clipDate = "Date Added: ${LocalDate.now()}"
                    app.clips.create(clip.copy())
                }
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(it, "Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }

        registerMapCallback()
        registerAudioPickerCallback()

    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clip, menu)
        if (userClip) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                i("delete pressed: $clip")
                app.clips.delete(clip)
                setResult(99)
                finish()
            }
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerAudioPickerCallback() {
        audioIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            i("Got Result $result")
                            clip.audio = result.data!!.data!!
                            binding.chooseAudio.setText(R.string.button_changeAudio)
                            binding.playBtn.isVisible = true
                            binding.stopBtn.isVisible = true
                            binding.pauseBtn.isVisible = true
                        }
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
                            i("Got Location ${result.data.toString()}")
                            //location = result.data!!.extras?.getParcelable("location",Location::class.java)!!
                            clip.location = result.data!!.extras?.getParcelable("userLocation")!!
                            i("Location == ${clip.location}")
                        } // end of if
                    }
                    RESULT_CANCELED -> { i("Cancel") }
                    else -> { i("else") }
                }
            }
    }

}