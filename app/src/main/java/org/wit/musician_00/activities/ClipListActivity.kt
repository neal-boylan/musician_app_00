package org.wit.musician_00.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.musician_00.R
import org.wit.musician_00.adapters.ClipAdapter
import org.wit.musician_00.adapters.ClipListener
import org.wit.musician_00.databinding.ActivityClipListBinding
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserModel
import timber.log.Timber.i

class ClipListActivity : AppCompatActivity(), ClipListener {

    var user = UserModel()
    lateinit var app: MainApp
    private lateinit var binding: ActivityClipListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClipListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        user = intent.extras?.getParcelable("user_details")!!
        i("This user: $user")

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = ClipAdapter(app.clips.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, ClipActivity::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_logout -> {
                val launcherIntent = Intent(this, LoginActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0,app.clips.findAll().size)
            }
        }

    override fun onClipClick(clip: ClipModel) {
        val launcherIntent = Intent(this, ClipActivity::class.java)
        launcherIntent.putExtra("clip_edit", clip)
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0,app.clips.findAll().size)
            }
        }
}
