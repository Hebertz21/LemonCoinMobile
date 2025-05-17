package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.classeObjetos.RvMovimentacoesClasse
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.ListaDespesasAdapter
import com.example.lemoncoin.databinding.FragmentDespesasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DespesasFragment : Fragment() {

    private var _binding: FragmentDespesasBinding? = null
    private val binding get() = _binding!! // Uso seguro do binding

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDespesasBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddDespesas.setOnClickListener(){
            trocarFragment(AddDespesasFragment())
        }

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbMovimentacoes = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("movimentações")
        Log.i(null, "Terminou a busca de movimentações")

        val listaDespesas: MutableList<RvMovimentacoesClasse> = mutableListOf()

        dbMovimentacoes.orderBy("data").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    Log.i(null, "entrou no ID: ${document.id}")
                    val categoriaId = document.get("categoriaId")?.toString()
                    val contaId = document.getString("contaId")
                    val data = document.getDate("data")
                    val nome = document.getString("nome")
                    val tipo = document.getString("tipo")
                    val valor = document.getDouble("valor")

                    if (categoriaId != null && contaId != null
                        && data != null && nome != null
                        && tipo != null && valor != null)
                    {
                        if (tipo == "Despesa") {
                            val movimentacao = RvMovimentacoesClasse(
                                nome = nome,
                                valor = valor,
                                data = data,
                                categoria = categoriaId,
                                conta = contaId,
                                tipo = tipo,
                                id = document.id
                            )
                            listaDespesas.add(movimentacao)
                        }
                    } else {
                        Log.w("Firestore", "Documento com campos nulos: ${document.id}")
                    }
                }
                Log.i(null, "lista de movimentações: $listaDespesas")
                val adapter = ListaDespesasAdapter(listaDespesas) { movimentacaoSelecionada ->
                    val fragment = AddDespesasFragment.newInstance(movimentacaoSelecionada.id)
                    trocarFragment(fragment)
                }
                binding.rvListaDespesas.layoutManager = LinearLayoutManager(requireContext())
                binding.rvListaDespesas.adapter = adapter

                val valorTotal = listaDespesas.sumOf { it.valor }
                val totalFormatado = NumberFormat
                    .getCurrencyInstance(Locale("pt", "BR")).format(valorTotal)
                binding.txtValorTotal.text = totalFormatado
            } else {
                Log.w("Firestore", "Erro ao obter movimentações", it.exception)
            }
        }
    }

    private fun trocarFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerMovimentacoes, fragment)
            .addToBackStack(null) //
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}