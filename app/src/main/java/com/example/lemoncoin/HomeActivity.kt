package com.example.lemoncoin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.fragments.CategoriasFragment
import com.example.lemoncoin.fragments.ContasFragment
import com.example.lemoncoin.fragments.HomeFragment
import com.example.lemoncoin.fragments.RelatoriosFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

//import com.example.lemoncoin.fragments.

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding //binding criado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)  //inflando o layout da tela
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navView) { v, insets ->  //NavView colocado para impedir que a Toolbar fique bugada
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null){ //Verifica se o layout está vazio
            val fragment = HomeFragment() //Define o fragment incial como home fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }

        // Configuração do botão para abrir/fechar o menu lateral
        binding.btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) { //Se estiver aberta, o bindinf fecha
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {                                                      //Se estiver fechada, o binding abre
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Configuração do clique para abrir o fragmento Contas
        binding.txtContas.setOnClickListener { //Função do clique Contas
            openFragment(ContasFragment())
        }

        /*//Despesas
        binding.txtDespesas.setOnClickListener {
            openFragment(RelatoriosFragment())
        }*/

        //Relatorios
        binding.txtRelatorios.setOnClickListener {
            openFragment(RelatoriosFragment())
        }

        //Categorias
        binding.txtCategorias.setOnClickListener {
            openFragment(CategoriasFragment())
        }

        binding.include.imgLogo.setOnClickListener {
            openFragment(HomeFragment())
        }

        binding.includeButtonLogout.btnLogout.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Firebase.auth.signOut()
            finish()
        }
    }

    //função para abrir fragment
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction() //
            .replace(R.id.fragmentContainer, fragment)
            /*Replace substitui, ele pega o R.id e coloca
             o id recebido atraves do argumento que foi passado */

            .addToBackStack(null) // Permite voltar ao fragmento anterior depois de apertar
            //o voltar do celular

            .commit()                   //Finaliza a aplicação
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}