package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R

class CategoriasFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            return inflater.inflate(  //Cria a visualização
                R.layout.fragment_categorias,  //R chama o layout, o containerView
                container,
                false // anexa ao elemento raiz automaticamente
            )
    }
}