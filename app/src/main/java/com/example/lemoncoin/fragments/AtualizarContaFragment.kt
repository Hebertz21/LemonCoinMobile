package com.example.lemoncoin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lemoncoin.databinding.FragmentAtualizarContaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        binding.btnConfirmar.setOnClickListener {
            val nome = binding.textViewConta.text.toString().trim()
            val saldoText = binding.inputSaldo.text.toString().trim()
            val descricao = binding.InputDescricao.text.toString().trim()
            val saldo = saldoText.toDoubleOrNull()

            if (nome.isEmpty() || saldo == null) {
                Toast.makeText(context, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null || contaId == null) return@setOnClickListener

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

                    val img = when(nome) {
                        "Banco do Brasil" -> R.drawable.banco_do_brasil
                        "Bradesco" -> R.drawable.bradesco
                        "Caixa" -> R.drawable.caixa
                        "Inter" -> R.drawable.inter
                        "Itaú" -> R.drawable.itau
                        "Mercado Pago" -> R.drawable.mercado_pago
                        "Nubank" -> R.drawable.nubank
                        "PicPay" -> R.drawable.picpay
                        "Santander" -> R.drawable.santander
                        "Sicredi" -> R.drawable.sicredi
                        "Stone" -> R.drawable.stone
                        "Wise" -> R.drawable.wise
                        else -> R.drawable.lapis
                    }

                    binding.textViewConta.setText(nome)
                    binding.inputSaldo.setText(saldo?.toString() ?: "")
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
