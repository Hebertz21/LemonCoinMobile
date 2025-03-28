package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
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