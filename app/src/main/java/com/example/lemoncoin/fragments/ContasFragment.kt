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
import com.google.firebase.firestore.ListenerRegistration


class ContasFragment : Fragment() {  //É preciso um constructor vazio para a classe enviar informações de configurações

    private var _binding: FragmentContasBinding? = null
    private val binding get() = _binding!!

    private var contasListener: ListenerRegistration? = null

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

        binding.txtSemConta.visibility = View.GONE

        contasListener()

        binding.btnAddContas.setOnClickListener(){
            openFragment(AddContasFragment())
        }

    }

    private fun contasListener() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbContas = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("contas") //contas do usuário atual
        Log.i(null, "Terminou a busca de contas")

        contasListener = dbContas.orderBy("nome", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ContasFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("ContasFragment", "Binding nulo em FragmentContas (snapshot), retornando.")
                    return@addSnapshotListener
                }

                val listaContas: MutableList<Conta> = mutableListOf()
                if (snapshots != null) {
                    for (document in snapshots) {
                        Log.i(null, "entrou no ID: ${document.id}")
                        val nome = document.getString("nome")
                        val saldo = document.getDouble("saldo")
                        Log.i("Firestore", "nome = $nome | saldo = $saldo")

                        val img = when (nome) {
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
                            "Vivo" -> R.drawable.vivo
                            "Cofre Pessoal" -> R.drawable.cofre
                            else -> R.drawable.lapis
                        }

                        listaContas.add(Conta(nome ?: "", saldo ?: 0.0, img, document.id))

                    }
                    Log.i(null, "lista de contas: $listaContas")
                    val adapter = ContaAdapter(listaContas) { contaSelecionada -> //onContaClick
                        val fragment = AtualizarContaFragment.newInstance(contaSelecionada.id)
                        openFragment(fragment)
                    }
                    binding.recyclerContas.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.recyclerContas.adapter = adapter

                }
                if (listaContas.isEmpty()) {
                    binding.txtSemConta.visibility = View.VISIBLE
                    binding.txtSemConta.setOnClickListener() {
                        openFragment(AddContasFragment())
                    }
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
        contasListener?.remove() // Remove o listener para evitar vazamentos de memória e chamadas desnecessárias
        _binding = null // Evita vazamento de memória
    }
}