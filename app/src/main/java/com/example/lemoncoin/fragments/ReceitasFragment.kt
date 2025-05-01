package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.ClasseObjetos.RvMovimentacoesClasse
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.ListaDespesasAdapter
import com.example.lemoncoin.databinding.FragmentReceitasBinding

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

        binding.btnAddReceitas.setOnClickListener(){
            trocarFragment(AddReceitasFragment())
        }

        val listaDespesas = listOf(
            RvMovimentacoesClasse("Conta de Luz", 100.0, "01/05/2025", "Despesas", "Bradesco"),
            RvMovimentacoesClasse("Aluguel", 1200.0, "01/05/2025", "Despesas", "Banco do brasil"),
            RvMovimentacoesClasse("Venda", 2000.0, "02/05/2025", "Receitas", "PicPay")
        )

        binding.rvListaReceitas.adapter = ListaDespesasAdapter(listaDespesas)
        binding.rvListaReceitas.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    private fun trocarFragment(fragment: androidx.fragment.app.Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerRelatorios, fragment)
            .addToBackStack(null) //
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}