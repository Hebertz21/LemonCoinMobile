package com.example.lemoncoin.fragments

import ListaContas
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddContaBinding
import com.example.lemoncoin.databinding.FragmentContasBinding

class AddContasFragment : Fragment() {  //É preciso um constructor vazio para a classe enviar informações de configurações

    private var _binding: FragmentAddContaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContaBinding.inflate(inflater,
            container, false)

        binding.imgAddConta.setOnClickListener(){
            val dialog = ListaContas()
            dialog.show(parentFragmentManager, "listaContas")
        }

        binding.InputDescricao.movementMethod = ScrollingMovementMethod()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }
}