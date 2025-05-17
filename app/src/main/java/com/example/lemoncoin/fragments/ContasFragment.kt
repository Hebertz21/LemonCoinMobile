package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lemoncoin.AtualizarContaFragment
import com.example.lemoncoin.adapters.ContaAdapter
import com.example.lemoncoin.classeObjetos.Conta
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentContasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ContasFragment : Fragment() {  //É preciso um constructor vazio para a classe enviar informações de configurações

    private var _binding: FragmentContasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContasBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buscarContas()

        binding.btnAddContas.setOnClickListener(){
            openFragment(AddContasFragment())
        }

    }

    private fun buscarContas() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbContas = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("contas") //contas do usuário atual
        Log.i(null, "Terminou a busca de contas")

        val listaContas: MutableList<Conta> = mutableListOf()

        dbContas.orderBy("nome", Query.Direction.ASCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    Log.i(null, "entrou no ID: ${document.id}")
                    val nome = document.getString("nome")
                    val saldo = document.getDouble("saldo")
                    Log.i("Firestore", "nome = $nome | saldo = $saldo")

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

                    if (nome != null && saldo != null) {
                        listaContas.add(Conta(nome, saldo, img, document.id))

                    } else {
                        Log.w("Firestore", "Documento com campos nulos: ${document.id}")
                    }
                }
                Log.i(null, "lista de contas: $listaContas")
                val adapter = ContaAdapter(listaContas) { contaSelecionada -> //quado clica na conta
                    val fragment = AtualizarContaFragment.newInstance(contaSelecionada.id)
                    openFragment(fragment)
                }
                binding.recyclerContas.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerContas.adapter = adapter

            } else {
                Log.e("Firestore", "Erro ao buscar documentos", it.exception)
            }
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null) //
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }
}