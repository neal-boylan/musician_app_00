package org.wit.musician_00.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.musician_00.databinding.ActivityClipBinding
import org.wit.musician_00.models.ClipModel
import timber.log.Timber
import timber.log.Timber.i

class ClipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClipBinding
    var clip = ClipModel()
    val clips = ArrayList<ClipModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("Clip Activity started..")

        binding.btnAdd.setOnClickListener() {
            clip.title = binding.clipTitle.text.toString()
            clip.description = binding.clipDescription.text.toString()
            if (clip.title.isNotEmpty()) {
                i("add Button Pressed")
                clips.add(clip.copy())

                for (index in clips.indices){
                    i("$index: ${clips[index].title}, ${clips[index].description}")
                }
            }
            else {
                Snackbar
                    .make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }
}