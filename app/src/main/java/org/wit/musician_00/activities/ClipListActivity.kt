package org.wit.musician_00.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
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
import org.wit.musician_00.activities.UserMapsActivity
import timber.log.Timber.i
import java.util.Locale



class ClipListActivity : AppCompatActivity(), ClipListener {
    private var pos: Int = 0
    var user = UserModel()
    lateinit var app: MainApp
    private lateinit var binding: ActivityClipListBinding
    // private lateinit var searchview: SearchView
    // private lateinit var adapter: ClipAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = intent.extras?.getParcelable("user_details")!!
        binding = ActivityClipListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Welcome, ${user.email}"
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = ClipAdapter(app.clips.findAll(), this)

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true

            }
        })
    }

    private fun filterList(query : String?){

        if (query != null) {
            val filteredList = ArrayList<ClipModel>()
            for (i in app.clips.findAll()){
                if (i.title.lowercase(Locale.ROOT).contains(query)){
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()){
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            }else{
                binding.recyclerView.adapter = ClipAdapter(filteredList, this)
                // adapter.setFilteredList(filteredList)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_map -> {
                val launcherIntent = Intent(this, UserMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
            R.id.item_add -> {
                val launcherIntent = Intent(this, ClipActivity::class.java).putExtra(
                    "user_details",
                    user
                )
                getResult.launch(launcherIntent)
            }
            R.id.item_logout -> {
                val launcherIntent = Intent(this, LoginActivity::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_user_details -> {
                val launcherIntent = Intent(this, UserActivity::class.java).putExtra(
                    "user_details",
                    user
                )
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

    override fun onClipClick(clip: ClipModel, position: Int) {
        val launcherIntent = Intent(this, ClipActivity::class.java)
        launcherIntent.putExtra("clip_edit", clip)
        launcherIntent.putExtra("user_details", user)
        pos = position
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(0,app.clips.findAll().size)
            }
            else if (it.resultCode == 99) {
                (binding.recyclerView.adapter)?.notifyItemRemoved(pos)
            }
        }

    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )    { }
}
