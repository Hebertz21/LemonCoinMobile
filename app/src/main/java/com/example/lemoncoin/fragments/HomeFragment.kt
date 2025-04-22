package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentDespesasBinding
import com.example.lemoncoin.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,
            container, false)

        binding.btnAddDespesaHome.setOnClickListener(){
            trocarFragment(AddDespesasFragment())
        }

        binding.btnAddReceitaHome.setOnClickListener(){
            trocarFragment(AddReceitasFragment())
        }

            return binding.root
    }
    private fun trocarFragment(fragment: androidx.fragment.app.Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }
}