package com.example.lemoncoin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: entrou no create da main")
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //code
        //supportActionBar?.hide() //esconde a actionBar
    }
    fun toHome(view: View) {
        if (this.localClassName != "MainActivity") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_main)
    }

    fun toLogin(view: View){
        setContentView(R.layout.activity_login)
    }
    fun toCadastro(View: View){
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: entrou no onStart")
    }
}