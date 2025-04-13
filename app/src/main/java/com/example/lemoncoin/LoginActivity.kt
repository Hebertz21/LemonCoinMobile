package com.example.lemoncoin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lemoncoin.databinding.ActivityLoginBinding
import com.example.lemoncoin.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnEntrar.setOnClickListener(){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.hpkSemConta.setOnClickListener(){
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}