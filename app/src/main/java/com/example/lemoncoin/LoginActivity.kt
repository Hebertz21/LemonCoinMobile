package com.example.lemoncoin

import EsqueciSenha
import ListaContas
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lemoncoin.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.callbackFlow

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

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

        fun autenticar(callback: (Boolean) -> Unit) {
            val email = binding.inputUsuario.text.toString()
            val senha = binding.inputSenha.text.toString()
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return
            }
            val auth = FirebaseAuth.getInstance()

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("FirebaseTest", "Autenticação bem-sucedida: ${auth.currentUser?.email}")
                        callback(true)
                    } else {
                        Log.e("FirebaseTest", "Erro na autenticação: ${task.exception?.message}")
                        callback(false)
                    }
                }
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnEntrar.setOnClickListener(){
            autenticar { sucesso ->
                if (sucesso) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuário ou senha incorreto", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun abrirCadastro(){
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        fun abrirEsqueciSenha(){
            val dialog = EsqueciSenha()
            dialog.show(supportFragmentManager, "esqueci_senha")
        }

        //Cadastro
        binding.hpkSemConta.setOnClickListener(){
            abrirCadastro()
        }

        binding.linhaNaoPossuoConta.setOnClickListener(){
            abrirCadastro()
        }

        //Esqueci Senha
        binding.hpkEsqueciSenha.setOnClickListener(){
            abrirEsqueciSenha()
        }
        binding.linhaEsqueciSenha.setOnClickListener(){
            abrirEsqueciSenha()
        }

        binding.toolbar.imgLogo.setOnClickListener(){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}