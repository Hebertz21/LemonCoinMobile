package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lemoncoin.Adapters.ContaAdapter
import com.example.lemoncoin.ClasseObjetos.Conta
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentContasBinding

class ContasFragment : Fragment() {  //É preciso um constructor vazio para a classe enviar informações de configurações

    private var _binding: FragmentContasBinding? = null
    private val binding get() = _binding!!

    private val listaContas = listOf(
        Conta("Nubank", "R$ 800,00", R.drawable.nubank),
        Conta("PicPay", "R$ 300,00", R.drawable.picpay)
    )

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

        binding.btnAddContas.setOnClickListener(){
            trocarFragment(AddContasFragment())
        }

        val adapter = ContaAdapter(listaContas)
        binding.recyclerContas.layoutManager = GridLayoutManager(requireContext(), 2) // 2 colunas
        binding.recyclerContas.adapter = adapter
    }
    private fun trocarFragment(fragment: androidx.fragment.app.Fragment) {
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