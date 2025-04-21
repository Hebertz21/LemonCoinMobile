package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentRelatoriosBinding

class RelatoriosFragment : Fragment() {
    private var _binding: FragmentRelatoriosBinding? = null
    private val binding get() = _binding!! // Uso seguro do binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelatoriosBinding.inflate(inflater, container, false)

        binding.txtDespesas.setOnClickListener {
            openFragment(DespesasFragment())
        }

        //Chama Receitas
        binding.txtReceitas.setOnClickListener() {
            openFragment(ReceitasFragment())
        }

        return binding.root
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerRelatorios, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}


