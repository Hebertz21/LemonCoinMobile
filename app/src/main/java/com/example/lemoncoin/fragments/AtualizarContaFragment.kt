package com.example.lemoncoin

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lemoncoin.databinding.FragmentAtualizarContaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors

class AtualizarContaFragment : Fragment() {

    companion object {
        private const val ARG_CONTA_ID = "conta_id"

        fun newInstance(contaId: String): AtualizarContaFragment {
            val fragment = AtualizarContaFragment()
            val args = Bundle()
            args.putString(ARG_CONTA_ID, contaId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentAtualizarContaBinding? = null
    private val binding get() = _binding!!

    private var contaId: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var nomeConta: String = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contaId = arguments?.getString(ARG_CONTA_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtualizarContaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarDadosConta()

        val dbContas = firestore
            .collection("usuarios")
            .document(userId.toString())
            .collection("contas")
            .document(contaId.toString())


        dbContas.get().addOnSuccessListener { document ->
            if (document.exists()) {
                nomeConta = document.getString("nome").toString()
            } else{
                nomeConta = ""
            }
        }

        binding.inputSaldo.addMoneyMask()

        binding.btnConfirmar.setOnClickListener {
            val nome = binding.textViewConta.text.toString().trim()
            val descricao = binding.InputDescricao.text.toString().trim()
            val saldoText = binding.inputSaldo.text.toString()
                // Remove caracteres não numéricos (R$, ponto e virgula)
                .replace("[R$,.\\s]".toRegex(), "")

            if (nome.isEmpty() || saldoText.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos corretamente",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null || contaId == null) return@setOnClickListener

            val saldo = saldoText.toDouble() / 100 //divide por 100 para salvar os centavos

            val dadosAtualizados = mapOf( //O que atualiza e manda para DB
                "nome" to nome,
                "saldo" to saldo,
                "descricao" to descricao
            )

            firestore.collection("usuarios")
                .document(userId)
                .collection("contas")
                .document(contaId!!)
                .update(dadosAtualizados)
                .addOnSuccessListener {
                    Toast.makeText(context, "Conta atualizada com sucesso", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.popBackStack() // Volta para tela anterior
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao atualizar conta", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnExcluir.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Excluir conta")
                .setMessage("Deseja realmente excluir a conta $nomeConta?")
                .setPositiveButton("Sim") { dialog, _ ->

                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        firestore.collection("usuarios")
                            .document(userId.toString())
                            .collection("contas")
                            .document(contaId.toString())
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Conta excluída com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao excluir conta: $e",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()

            dialog.setOnShowListener {
                val btnSim = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val btnNao = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                btnSim.setTextColor(ContextCompat.getColor(requireContext(), R.color.textView))
                btnNao.setTextColor(ContextCompat.getColor(requireContext(), R.color.textView))
            }

            dialog.show()
        }

    }

    private fun carregarDadosConta() {
        if (userId == null || contaId == null) return

        firestore.collection("usuarios")
            .document(userId)
            .collection("contas")
            .document(contaId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome")
                    val saldo = document.getDouble("saldo")
                    val descricao = document.getString("descricao")
                    val imgId = document.getLong("imgId")?.toInt()
                    var img : Int
                    binding.imgConta.setPadding(0,0,0,0)

                    img = when (nome) {
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

                    val saldoFormatado = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR")).format(saldo)

                    binding.textViewConta.setText(nome)
                    binding.inputSaldo.setText(saldoFormatado)
                    binding.InputDescricao.setText(descricao)
                    binding.imgConta.setImageResource(img)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro ao carregar dados da conta", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
