package com.example.lemoncoin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddReceitasBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddReceitasFragment : Fragment() {

    private var _binding: FragmentAddReceitasBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private var modo = "add"

    private var listaCategoriasId: MutableList<String> = mutableListOf()
    private var listaContasId: MutableList<String> = mutableListOf()
    private var categoriaIdRecebida: String? = null
    private var contaIdRecebida: String? = null

    companion object { //caso abra a tela no modo editar
        private const val ARG_Receita_ID = "receita_id"

        fun newInstance(id: String): AddReceitasFragment {
            val fragment = AddReceitasFragment()
            val args = Bundle()
            args.putString(ARG_Receita_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    private fun carregarDadosReceita() {
        Log.i(null, "id enviado: ${arguments?.getString(ARG_Receita_ID)}")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val movId = arguments?.getString(ARG_Receita_ID) ?: return
        modo = "edit"

        firestore.collection("usuarios")
            .document(userId)
            .collection("movimentações")
            .document(movId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val valor = document.getDouble("valor")
                    val valorFormatado = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR")).format(valor)
                    val data = document.getDate("data")
                    categoriaIdRecebida = document.getString("categoriaId")
                    contaIdRecebida = document.getString("contaId")

                    binding.inputNomeReceita.setText(nome)
                    binding.inputValorReceita.setText(valorFormatado.toString())
                    val dataFormatada = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()).format(data)
                    binding.etDataReceitas.setText(dataFormatada)
                }
            }

        binding.btnAddReceita.text = "EDITAR"
        binding.txtTitulo.text = "EDITAR RECEITA"
    }

    private fun EditText.addMoneyMask() {
        val locale = Locale("pt", "BR")
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)

        this.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    this@addMoneyMask.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "").trim()
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

    override fun onCreateView( //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddReceitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarDadosReceita()

        binding.etDataReceitas.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setTheme(R.style.datePicker)
                .build()
            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
                binding.etDataReceitas.setText(dataFormatada)
            }
            datePicker.show(parentFragmentManager, "Date_Picker")
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        //configuração do spinner de categorias
        val categoriasNome: MutableList<String> = mutableListOf("Selecione Categoria")
        var listaCategoriasId : MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    val nome = doc.getString("nome") ?: ""
                    categoriasNome.add(nome)
                    listaCategoriasId.add(doc.id)
                }

                val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoriasNome)
                binding.spnCategoriaReceita.adapter = categoriaAdapter

                if (modo == "edit") {

                    val pos = listaCategoriasId.indexOf(categoriaIdRecebida)
                    if (pos >= 0) binding.spnCategoriaReceita.setSelection(pos + 1)
                }
            }


        //configuração do spinner de contas
        val contasNome: MutableList<String> = mutableListOf("Selecione Conta")
        var listaContasId : MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("contas")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    val nome = doc.getString("nome") ?: ""
                    contasNome.add(nome)
                    listaContasId.add(doc.id)
                }

                val contaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, contasNome)
                binding.spnContaReceita.adapter = contaAdapter

                if (modo == "edit") {
                    val pos = listaContasId.indexOf(contaIdRecebida)
                    if (pos >= 0) binding.spnContaReceita.setSelection(pos + 1)
                }
            }

        binding.inputValorReceita.addMoneyMask()

        binding.btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddReceita.setOnClickListener {
            val nome = binding.inputNomeReceita.text.toString()
            val txtValor = binding.inputValorReceita.text.toString()
            val txtData = binding.etDataReceitas.text.toString()
            val categoriaPosition = binding.spnCategoriaReceita.selectedItemPosition
            val contaPosition = binding.spnContaReceita.selectedItemPosition

            if (nome.isNotEmpty() &&
                txtValor.isNotEmpty() &&
                txtData.isNotEmpty() &&
                categoriaPosition > 0 &&
                contaPosition > 0
            ) {
                val categoriaId = listaCategoriasId[categoriaPosition - 1]
                val contaId = listaContasId[contaPosition - 1]
                val valor = txtValor.replace("[R$,.\\s]".toRegex(), "").trim().toDouble() / 100
                val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(txtData)

                val receita = hashMapOf(
                    "nome" to nome,
                    "valor" to valor,
                    "data" to data,
                    "categoriaId" to categoriaId,
                    "contaId" to contaId,
                    "tipo" to "Receita"
                )

                if (modo == "add") {
                    //adicionar receita
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .collection("movimentações")
                        .add(receita)
                    //modificar valor de conta
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .collection("contas")
                        .document(contaId)
                        .update("saldo", FieldValue.increment(valor))
                    parentFragmentManager.popBackStack()
                    Toast.makeText(requireContext(),
                        "Receita adicionada com sucesso!", Toast.LENGTH_SHORT).show()

                } else if (modo == "edit") {
                    val receitaId = arguments?.getString(ARG_Receita_ID) ?: return@setOnClickListener

                    // Buscar o valor atual da receita
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .collection("movimentações")
                        .document(receitaId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val valorAntigo = documentSnapshot.getDouble("valor") ?: 0.0

                            //editar receita
                            FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .collection("movimentações")
                                .document(receitaId).set(receita)

                            //modificar valor de conta
                            val diferencaValor = valor - valorAntigo
                            FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .collection("contas")
                                .document(contaId)
                                .update(
                                    "saldo",
                                    FieldValue.increment(diferencaValor)
                                ) //adiciona a diferença


                            parentFragmentManager.popBackStack()
                            Toast.makeText(requireContext(),
                                "Receita atualizada com sucesso!", Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}