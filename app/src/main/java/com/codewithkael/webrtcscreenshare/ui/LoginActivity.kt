package com.codewithkael.webrtcscreenshare.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codewithkael.webrtcscreenshare.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    lateinit var views: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)

        views.enterBtn.setOnClickListener {
            if (views.usernameEt.text.isNullOrEmpty()){
                Toast.makeText(this, "please fill the username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(
                Intent(this,MainActivity::class.java).apply {
                    putExtra("username",views.usernameEt.text.toString())
                }
            )
        }

    }
}