package com.example.lemoncoin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.fragments.ContasFragments

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding //binding criado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)  //inflando o layout da tela
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuração do botão para abrir/fechar o menu lateral
        binding.btnHome.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) { //Se estiver aberta, o bindinf fecha
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {                                                      //Se estiver fechada, o binding abre
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Configuração do clique para abrir o fragmento Contas
        binding.txtContas.setOnClickListener { //Função do clique Contas
            openFragment(ContasFragments())
        }
    }

    // Método para abrir fragmentos
    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction() //
            .replace(R.id.fragmentContas, fragment)
            .addToBackStack(null) // Permite voltar ao fragmento anterior depois de apertar
                                        //o voltar do celular
            .commit()                   //Finaliza a aplicação
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}

