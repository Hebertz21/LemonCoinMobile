package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R

class AddDespesasFragment : Fragment() {

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(   //Cria a visualização
            R.layout.fragment_add_despesas,  //R chama o layout, o containerView e
            container,
            false // anexa ao elemento raiz automaticamente
        )
    }
}