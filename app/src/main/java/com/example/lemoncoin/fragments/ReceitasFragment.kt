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
import com.example.lemoncoin.adapters.ListaReceitasAdapter
import com.example.lemoncoin.databinding.FragmentReceitasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class ReceitasFragment : Fragment() {

    private var _binding: FragmentReceitasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceitasBinding.inflate(inflater,
            container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddReceitas.setOnClickListener(){
            trocarFragment(AddReceitasFragment())
        }

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbMovimentacoes = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("movimentações")
        Log.i(null, "Terminou a busca de movimentações")

        val listaReceitas : MutableList<RvMovimentacoesClasse> = mutableListOf()

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
                        if (tipo == "Receita") {
                            val movimentacao = RvMovimentacoesClasse(
                                nome = nome,
                                valor = valor,
                                data = data,
                                categoria = categoriaId,
                                conta = contaId,
                                tipo = tipo,
                                id = document.id
                            )
                            listaReceitas.add(movimentacao)
                        }
                    } else {
                        Log.w("Firestore", "Documento com campos nulos: ${document.id}")
                    }
                }
                val adapter = ListaDespesasAdapter(listaReceitas) { movimentacaoSelecionada ->
                    val fragment = AddReceitasFragment.newInstance(movimentacaoSelecionada.id)
                    trocarFragment(fragment)

                }
                binding.rvListaReceitas.layoutManager = LinearLayoutManager(requireContext())
                binding.rvListaReceitas.adapter = adapter

                val valorTotal = listaReceitas.sumOf { it.valor }
                val totalFormatado = NumberFormat
                    .getCurrencyInstance(Locale("pt", "BR")).format(valorTotal)
                binding.txtValorTotal.text = totalFormatado
            }
        }
    }

    private fun trocarFragment(fragment: androidx.fragment.app.Fragment){
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