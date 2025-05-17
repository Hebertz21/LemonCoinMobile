package com.example.lemoncoin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddDespesasBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddDespesasFragment : Fragment() {

    private var _binding: FragmentAddDespesasBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAddDespesasBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //configuração do datepicker
        binding.etDataDespesa.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setTheme(R.style.datePicker)
                .build()
            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataformatada = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(millis))
                binding.etDataDespesa.setText(dataformatada)
            }
            datePicker.show(parentFragmentManager, "Date_Picker")
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        //configuração do spinner de categorias
        var categoriasNome : MutableList<String> = mutableListOf("Selecione Categoria")
        var listaCategoriasId : MutableList<String> = mutableListOf()

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    categoriasNome.add(doc.getString("nome") ?: "")
                    listaCategoriasId.add(doc.id)
                }
            }

        val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoriasNome)
        binding.spnCategoriaDespesa.adapter = categoriaAdapter

        //configuração do spinner de contas
        var contasNome : MutableList<String> = mutableListOf("Selecione Conta")
        var listaContasId : MutableList<String> = mutableListOf()
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("contas")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    contasNome.add(doc.getString("nome") ?: "")
                    listaContasId.add(doc.id)
                }
            }
        val contaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, contasNome)
        binding.spnContaDespesa.adapter = contaAdapter

        binding.inputValorDespesa.addMoneyMask()

        binding.btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddDespesa.setOnClickListener {
            val nome = binding.inputNomeDespesa.text.toString()
            val txtValor = binding.inputValorDespesa.text.toString()
            val txtData = binding.etDataDespesa.text.toString()
            val categoriaPosition = binding.spnCategoriaDespesa.selectedItemPosition
            val contaPosition = binding.spnContaDespesa.selectedItemPosition

            if (nome.isNotEmpty() &&
                txtValor.isNotEmpty() &&
                txtData.isNotEmpty() &&
                categoriaPosition > 0 &&
                contaPosition > 0)
            {
                val categoriaId = listaCategoriasId[categoriaPosition - 1]
                val contaId = listaContasId[contaPosition - 1]
                val valor = txtValor.replace("[R$,.\\s]".toRegex(), "").trim().toDouble() / 100
                val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(txtData)

                val despesa = hashMapOf(
                    "nome" to nome,
                    "valor" to valor,
                    "data" to data,
                    "categoriaId" to categoriaId,
                    "contaId" to contaId,
                    "tipo" to "Despesa"
                )
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .collection("movimentações")
                    .add(despesa)

                parentFragmentManager.popBackStack()
                Toast.makeText(requireContext(),
                    "Despesa cadastrada com sucesso!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(),
                    "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}