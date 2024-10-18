package org.wit.musician_00.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.musician_00.R
import org.wit.musician_00.databinding.ActivityClipBinding
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.ClipModel
import timber.log.Timber.i

class ClipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClipBinding
    var clip = ClipModel()
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("Clip Activity started..")

        binding.btnAdd.setOnClickListener() {
            clip.title = binding.clipTitle.text.toString()
            clip.description = binding.clipDescription.text.toString()
            if (clip.title.isNotEmpty()) {
                i("add Button Pressed")
                app.clips.add(clip.copy())

                for (i in app.clips.indices)
                { i("Clip[$i]:${this.app.clips[i]}") }
                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar
                    .make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clip, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}