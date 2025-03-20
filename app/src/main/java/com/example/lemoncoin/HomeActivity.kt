package com.example.lemoncoin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.fragments.ContasFragment
import com.example.lemoncoin.fragments.DespesasFragment

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
        if (savedInstanceState == null){  //Verifica se o layout está vazio
            val fragment = DespesasFragment() //Define o fragment incial como DESPESAS
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
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
            openContasFragment(ContasFragment())
        }

        //Despesas
        binding.txtDespesas.setOnClickListener(){
            openDespesasFragments(DespesasFragment())
        }
    }

    // Metodo para o txtContas
    private fun openContasFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction() //
            .replace(R.id.fragmentContainer, fragment) /*Replace substitui, ele pega o R.id e coloca
             o id recebido atraves do argumento que foi passado, nesse foi (DespesasFragment)*/

            .addToBackStack(null) // Permite voltar ao fragmento anterior depois de apertar
            //o voltar do celular

            .commit()                   //Finaliza a aplicação
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
    //Funçãoo Despesas
    private fun openDespesasFragments(fragment: androidx.fragment.app.Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}