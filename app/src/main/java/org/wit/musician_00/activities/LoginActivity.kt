package org.wit.musician_00.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.wit.musician_00.databinding.ActivityLoginBinding
import org.wit.musician_00.main.MainApp
import org.wit.musician_00.models.ClipModel
import org.wit.musician_00.models.UserModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
//    lateinit var username : EditText
//    lateinit var password: EditText
//    lateinit var loginButton: Button
    var user = UserModel()
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        binding.loginButton.setOnClickListener(View.OnClickListener {
            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "1234"){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                user.email = binding.username.text.toString()
                user.password = binding.password.text.toString()
                app.users.create(user.copy())
                setResult(RESULT_OK)
                val launcherIntent = Intent(this, ClipListActivity::class.java)
                startActivity(launcherIntent)
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })

    }


}