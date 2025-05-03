package com.example.lemoncoin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.lemoncoin.databinding.ActivityCadastroBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.santalu.maskara.Mask
import com.santalu.maskara.MaskChangedListener
import com.santalu.maskara.MaskStyle


class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()

        fun cadastrarUsuario(
            email: String,
            senha: String,
            callback: (Boolean, String?, String?) -> Unit
        ) {
            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        callback(true, userId, null)
                    } else {
                        val erro = task.exception?.message
                        callback(false, null, erro)
                    }
                }
        }

        fun salvarDadosUsuario(
            userId: String,
            nome: String,
            email: String,
            telefone: String,
            dataNascimento: String,
            genero: String,
            callback: (Boolean, String?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()

            val dados = hashMapOf(
                "nome" to nome,
                "email" to email,
                "telefone" to telefone,
                "dataNascimento" to dataNascimento,
                "genero" to genero
            )

            firestore.collection("usuarios").document(userId)
                .set(dados)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        }

        binding = ActivityCadastroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mask = Mask(
            value = "(__) _____-____",
            character = '_',
            style = MaskStyle.PERSISTENT
        )
        val listener = MaskChangedListener(mask)
        binding.inputTelefone.addTextChangedListener(listener)

        binding.btnCadastrar.setOnClickListener() {
            val email = binding.inputEmail.text.toString().trim()
            val senha = binding.inputSenha.text.toString().trim()
            val confirmarSenha = binding.inputConfSenha.text.toString().trim()
            val nome = binding.inputNome.text.toString().trim()
            val dataNascimento = binding.etDataNascimento.text.toString().trim()
            val telefone = listener.masked

            var genero: String = ""
            if (binding.rbtnMasculino.isChecked) {
                genero = "M"
            } else if (binding.rbtnFeminino.isChecked) {
                genero = "F"
            } else if (binding.rbtnOutro.isChecked) {
                genero = "O"
            }

            if(email.isEmpty() ||
                senha.isEmpty() ||
                confirmarSenha.isEmpty() ||
                nome.isEmpty() ||
                telefone.isEmpty() ||
                dataNascimento.isEmpty() ||
                genero.isEmpty() ||
                !listener.isDone)
            {
                Toast.makeText(this,
                    "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (senha != confirmarSenha) {
                Toast.makeText(this,
                    "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarUsuario(email, senha) { sucesso, userId, erro ->
                if (sucesso && userId != null) {
                    salvarDadosUsuario(userId, nome, email, telefone, dataNascimento, genero)
                    { dadosSalvos, msgErro ->
                        if (dadosSalvos) {
                            Toast.makeText(
                                this,
                                "Cadastro feito com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Firebase.auth.signOut()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "erro ao salvar dados: $msgErro",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this,
                        "Erro ao cadastrar: $erro", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.toolbar.imgLogo.setOnClickListener() {
            finish()
        }

        binding.etDataNascimento.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data de nascimento")
                .setTheme(R.style.datePicker)
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(Date(millis))
                binding.etDataNascimento.setText(dataFormatada)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }
}