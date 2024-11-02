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
import timber.log.Timber
import timber.log.Timber.i

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
        var username = ""
        var password = ""

        binding.loginButton.setOnClickListener(View.OnClickListener {
            username = binding.username.text.toString()
            password = binding.password.text.toString()
            if (username != "" && password != "") {
                if (app.users.findByEmail(username)?.email != "") {
                    if (app.users.findByEmail(username)?.password == password) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        user = app.users.findByEmail(username)!!
                        setResult(RESULT_OK)
                        val launcherIntent = Intent(this, ClipListActivity::class.java).putExtra("user_details", user)
                        startActivity(launcherIntent)
                    } else {
                        i("password: $password")
                        i("app.users.findByEmail(username)?: ${app.users.findByEmail(username)}")
                        Toast.makeText(this, "Login Failed, password incorrect!", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this, "Login Failed, user not found!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Login Failed, username and password cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        })

        binding.registerButton.setOnClickListener(View.OnClickListener {
            username = binding.username.text.toString()
            password = binding.password.text.toString()
            if (username != "" && password != ""){
                if (app.users.findByEmail(username)?.email.isNullOrEmpty()) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    user.email = username
                    user.password = password
                    app.users.create(user.copy())
                    setResult(RESULT_OK)
                    val launcherIntent = Intent(this, LoginActivity::class.java)
                    startActivity(launcherIntent)
                } else {
                    Toast.makeText(this, "Account already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show()
            }
        })

    }


}