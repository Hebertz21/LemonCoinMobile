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
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddContaBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
        imgId: Int?,
        callback: (Boolean, String?) -> Unit
    ) {
        val userId = Firebase.auth.currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        val dados = hashMapOf(
            "nome" to nome,
            "saldo" to saldo,
            "descricao" to descricao,
            "imgId" to imgId
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
            val imgId = bundle.getInt("imgId")
            val txtConta = bundle.getString("txtConta")

            binding.imgConta.tag = imgId
            binding.imgConta.setImageResource(buscarImg(txtConta, imgId))
            binding.textViewConta.text = txtConta
            binding.imgConta.setPadding(0,0,0,0)
        }

        binding.imgAddConta.setOnClickListener(){
            val dialog = ListaContas()
            dialog.show(parentFragmentManager, "listaContas")
        }

        binding.textViewConta.setOnClickListener(){
            val dialog = ListaContas()
            dialog.show(parentFragmentManager, "listaContas")
        }

        binding.InputDescricao.movementMethod = ScrollingMovementMethod()

        //coloca uma mascara de real (R$) no input
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
            val imgId = binding.imgConta.tag.toString().toIntOrNull()

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
                        salvarDadosConta(nome, saldo, descricao, imgId) { sucesso, msgErro ->
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

    private fun buscarImg(txtConta : String?, imgId : Int) : Int {
        var img : Int
        img = when (txtConta) {
            "Banco do Brasil" -> R.drawable.banco_do_brasil
            "Bradesco" -> R.drawable.bradesco
            "Caixa" -> R.drawable.caixa
            "Inter" -> R.drawable.inter
            "Itaú" -> R.drawable.itau
            "Mercado Pago" -> R.drawable.mercado_pago
            "Nubank" -> R.drawable.nubank
            "PicPay" -> R.drawable.picpay
            "Rico" -> R.drawable.rico
            "Santander" -> R.drawable.santander
            "Sicredi" -> R.drawable.sicredi
            "Stone" -> R.drawable.stone
            "Wise" -> R.drawable.wise
            "Vivo" -> R.drawable.vivo
            "Cofre Pessoal" -> R.drawable.cofre

            else -> 0
        }
        if(img == 0) {
            img = when (imgId) {
                2131230886 -> R.drawable.generico_1
                2131230887 -> R.drawable.generico_2
                2131230888 -> R.drawable.generico_3
                2131230889 -> R.drawable.generico_4
                2131230890 -> R.drawable.generico_5
                2131230891 -> R.drawable.generico_6
                2131230892 -> R.drawable.generico_7
                2131230893 -> R.drawable.generico_8

                else -> R.drawable.lapis
            }
        }
        return img
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}