package com.example.lemoncoin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun testarLogin() {//verifica se o usuário já esta logado, se sim, leva direto pra tela home
            val usuario = FirebaseAuth.getInstance().currentUser

            if (usuario != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        testarLogin()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //code
        //supportActionBar?.hide() //esconde a actionBar

        binding.includeButtonLogin.btnLoginInicio.setOnClickListener(){
            try {
                FirebaseAuth.getInstance()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } catch (ex : Exception){
                Toast.makeText(null, "Houve um erro no servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}