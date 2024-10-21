package org.wit.musician_00.activities

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityClipBinding
import org.wit.musician_00.helpers.showAudioPicker
import org.wit.musician_00.helpers.showImagePicker
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.Location
import timber.log.Timber.i

class ClipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClipBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var audioIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mediaPlayer: MediaPlayer
    var clip = ClipModel()
    lateinit var app : MainApp
    var location = Location(52.245696, -7.139102, 5f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false
        binding = ActivityClipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("Clip Activity started..")
        mediaPlayer = MediaPlayer.create(this,R.raw.guitar_melody)

        i("clip.title: ${clip.title}")

        binding.stopBtn.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()

                mediaPlayer.prepare()
            }
        }
        binding.playBtn.setOnClickListener{
            mediaPlayer.start()
        }
        binding.pauseBtn.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        if (intent.hasExtra("clip_edit")) {
            edit = true
            clip = intent.extras?.getParcelable("clip_edit")!!
            i("This clip: $clip")
            binding.clipTitle.setText(clip.title)
            binding.clipDescription.setText(clip.description)
            binding.btnAdd.text = getString(R.string.button_saveClip)
            Picasso.get().load(clip.image).into(binding.clipImage)
            if (clip.image != Uri.EMPTY) {
                binding.chooseImage.text = getString(R.string.button_changeImage)
            }
            location = clip.location
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.chooseAudio.setOnClickListener {
            showAudioPicker(audioIntentLauncher)
        }

        registerImagePickerCallback()
        registerMapCallback()
        registerAudioPickerCallback()

        binding.btnAdd.setOnClickListener() {
            clip.title = binding.clipTitle.text.toString()
            clip.description = binding.clipDescription.text.toString()
            if (clip.title.isNotEmpty()) {
                if (edit) {
                    app.clips.update(clip.copy())
                } else {
                    app.clips.create(clip.copy())
                }
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar.make(it,"Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.clipLocation.setOnClickListener {
            val launcherIntent = Intent(this, MapActivity::class.java).putExtra("location", clip.location)
            mapIntentLauncher.launch(launcherIntent)
        }

    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clip, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            clip.image = result.data!!.data!!
                            Picasso.get().load(clip.image).into(binding.clipImage)
                            binding.chooseImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { }
                    else -> { }
                }
            }
    }

    private fun registerAudioPickerCallback() {
        audioIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            clip.audio = result.data!!.data!!
                            // Picasso.get().load(clip.image).into(binding.clipImage)
                            binding.chooseAudio.setText(R.string.button_changeAudio)
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
                            i("Got Location ${result.data.toString()}")
                            //location = result.data!!.extras?.getParcelable("location",Location::class.java)!!
                            clip.location = result.data!!.extras?.getParcelable("location")!!
                            i("Location == ${clip.location}")
                        } // end of if
                    }
                    RESULT_CANCELED -> { i("Cancel") }
                    else -> { i("else") }
                }
            }
    }

}