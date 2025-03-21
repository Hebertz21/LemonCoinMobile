package com.example.lemoncoin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView


class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        Log.d(TAG, "onCreate: entrou no create de cadastro")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //essa desgraça não funciona, não sei pq...
    override fun onStart() {
        Log.d(TAG, "onStart: entrou no start de cadastro")
        super.onStart()

        //criação do Spinner mes
        val spnMes: Spinner = findViewById<Spinner>(R.id.spnMes)

        // Lista de meses
        val meses = arrayOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        // Cria um ArrayAdapter com o layout padrão para o Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)

        // Define o layout para o dropdown (lista suspensa)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Define o adaptador no Spinner
        spnMes.adapter = adapter
    }
}