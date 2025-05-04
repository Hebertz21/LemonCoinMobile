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
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testarConexao()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.includeButtonLogin.btnLoginInicio.setOnClickListener(){
            try {
                testarConexao()
            } catch (ex : Exception){
                Toast.makeText(null, "Houve um erro no servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun testarConexao() {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").get()
            .addOnSuccessListener { result ->
                if(!(result.isEmpty)) {
                    Log.d("FIREBASE_TEST", "Conexão bem-sucedida! ${result.size()}")
                    testarLogin()
                } else {
                    Log.e("FIREBASE_TEST", "Erro ao conectar: sem rede")
                    Toast.makeText(this, "Erro ao conectar com banco de dados",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIREBASE_TEST", "Erro ao conectar: ${exception.message}")
                Toast.makeText(this, "Erro ao conectar com banco de dados",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun testarLogin() {//verifica se o usuário já esta logado, se sim, leva direto pra tela home
        val usuario = FirebaseAuth.getInstance().currentUser

        if (usuario != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}