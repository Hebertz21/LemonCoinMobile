package com.example.lemoncoin.fragments

import ListaContas
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddContaBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
//import com.example.lemoncoin.databinding.FragmentContasBinding

class AddContasFragment : Fragment() {  //É preciso um constructor vazio para a classe enviar informações de configurações

    private var _binding: FragmentAddContaBinding? = null
    private val binding get() = _binding!!

    private fun salvarDadosConta(
        nome: String,
        saldo: Number,
        descricao: String?, //? No final faz a var aceitar valores nulos
        callback: (Boolean, String?) -> Unit
    ) {
        val userId = Firebase.auth.currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val dados = hashMapOf(
            "nome" to nome,
            "saldo" to saldo,
            "descricao" to descricao
        )

        firestore.collection("usuarios")
            .document(userId.toString())
            .collection("contas")
            .add(dados)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    private fun contaExiste(nome: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = Firebase.auth.currentUser?.uid
        val dbContas = db.collection("usuarios")
            .document(userId.toString())
            .collection("contas")

        dbContas.get()
            .addOnSuccessListener { result ->
                var existe = false
                for (document in result) {
                    if (document.getString("nome") == nome) {
                        existe = true
                        break
                    }
                }
                callback(existe)
            }
    }

    private fun EditText.addMoneyMask(){
        val locale = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)

        this.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    this@addMoneyMask.removeTextChangedListener(this)

                    val cleanString = s.toString()
                        .replace("[R$,.\\s]".toRegex(), "")
                        .trim()

                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val formatted = currencyFormat.format(parsed / 100)

                    current = formatted
                    this@addMoneyMask.setText(formatted)
                    this@addMoneyMask.setSelection(formatted.length)

                    this@addMoneyMask.addTextChangedListener(this)
                }
            }
        })
    }

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContaBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val imgResId = bundle.getInt("imgResId")
            val txtConta = bundle.getString("txtConta")
            binding.imgConta.setImageResource(imgResId)
            binding.textViewConta.text = txtConta
            //binding.imgConta.setPadding(0,0,0,0)
        }

        binding.imgAddConta.setOnClickListener(){
            val dialog = ListaContas()
            dialog.show(parentFragmentManager, "listaContas")
        }

        binding.textViewConta.setOnClickListener(){
            val dialog = ListaContas()
            dialog.show(parentFragmentManager, "listaContas")
            //futuramente, adicionar logica para conta personalizada
        }

        binding.InputDescricao.movementMethod = ScrollingMovementMethod()

        //coloqua uma mascara de real (R$) no input
        binding.inputSaldo.addMoneyMask()

        binding.btnCancelar.setOnClickListener(){
            parentFragmentManager.popBackStack()
        }

        binding.btnConfirmar.setOnClickListener(){
            val txtSaldo = binding.inputSaldo.text.toString()
                // Remove caracteres não numéricos (R$, ponto e virgula)
                .replace("[R$,.\\s]".toRegex(), "")
            val descricao = binding.InputDescricao.text.toString()
            val nome = binding.textViewConta.text.toString()

            if(txtSaldo.isNotEmpty() && nome.isNotEmpty() && nome != "Conta"){
                contaExiste(nome) { existe ->
                    if (existe) {
                        Toast.makeText(
                            requireContext(),
                            "A conta ${nome} já existe!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //salvar dados da conta
                        val saldo = txtSaldo.toDouble() / 100 //divide por 100 para salvar os centavos
                        salvarDadosConta(nome, saldo, descricao) { sucesso, msgErro ->
                            if (sucesso) {
                                Toast.makeText(
                                    requireContext(),
                                    "Conta salva com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "erro ao salvar conta: $msgErro", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(),
                    "Preencha os campos de saldo e conta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}