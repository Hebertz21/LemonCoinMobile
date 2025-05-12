package com.example.lemoncoin.fragments

import CategoriasAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.classeObjetos.Categorias
import com.example.lemoncoin.databinding.FragmentCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriasBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbCategorias = db
            .collection("usuarios")
            .document(user?.uid.toString())
            .collection("categorias")
        Log.i(null, "Terminou a busca de categorias")
        Log.i(null, "1")

        val listaCategorias: MutableList<Categorias> = mutableListOf()

        dbCategorias.orderBy("nome").get().addOnCompleteListener{
            if (it.isSuccessful) {
                Log.i(null, "2")
                for (document in it.result){
                    Log.i(null, "3")
                    Log.i(null, "entrou no ID: ${document.id}")
                    val nome = document.getString("nome")
                    //val categoriaId = document.getString("Id")

                    if (nome != null){
                        Log.i(null, "4")

                        val categorias = Categorias(
                            nome = nome,
                            id = document.id
                        )
                        listaCategorias.add(categorias)
                    } else{
                        Log.w("Firestore", "Documento com campos nulos: ${document.id}")

                    }
                }
                Log.i(null, "lista de Categorias: $listaCategorias")
                val adapter = CategoriasAdapter(listaCategorias)
                binding.rvListaCategorias.layoutManager = LinearLayoutManager(requireContext())
                binding.rvListaCategorias.adapter = adapter

            }else{
                Log.w("Firestore", "Erro ao obter Categorias", it.exception)
                Log.i(null, "2.1")
            }

        }

    }
}