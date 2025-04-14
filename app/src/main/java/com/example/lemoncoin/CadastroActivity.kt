package com.example.lemoncoin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import com.example.lemoncoin.databinding.ActivityCadastroBinding


class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        binding = ActivityCadastroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Log.d(TAG, "onCreate: entrou no create de cadastro")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCadastrar.setOnClickListener(){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
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

        //criação do spinner de dias
        val spnDia: Spinner = findViewById<Spinner>(R.id.spnDia)

        //dias do mês. usa um for para adicionar de 1 a 31
        val dias = Array(31) { i -> (i + 1).toString() }
        val diaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dias)

        diaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnDia.adapter = diaAdapter

        //criação do spinner de anos
        val spnAno: Spinner = findViewById<Spinner>(R.id.spnAno)

        //a função puxa o ano atual
        val anoAtual = Calendar.getInstance().get(Calendar.YEAR)

        // Lista de anos
        val anos = Array(100) { i -> (anoAtual - i).toString() }
        val anoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anos)

        anoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnAno.adapter = anoAdapter
    }
}