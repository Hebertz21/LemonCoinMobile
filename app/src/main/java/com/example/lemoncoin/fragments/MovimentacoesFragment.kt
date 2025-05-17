package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentMovimentacoesBinding

class MovimentacoesFragment : Fragment() {
    private var _binding: FragmentMovimentacoesBinding? = null
    private val binding get() = _binding!! // Uso seguro do binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      _binding = FragmentMovimentacoesBinding.inflate(inflater, container, false)
      binding.txtDespesas.setOnClickListener {
            openFragment(DespesasFragment())
            binding.txtDespesas.backgroundTintList = null
            binding.txtDespesas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttonsPressed))
            binding.txtReceitas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttons))

        }

        //Chama Receitas
        binding.txtReceitas.setOnClickListener() {
            openFragment(ReceitasFragment())
            binding.txtReceitas.backgroundTintList = null
            binding.txtDespesas.backgroundTintList = null
            binding.txtReceitas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttonsPressed))
            binding.txtDespesas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.buttons))
        }

        return binding.root
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerMovimentacoes, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }

}


