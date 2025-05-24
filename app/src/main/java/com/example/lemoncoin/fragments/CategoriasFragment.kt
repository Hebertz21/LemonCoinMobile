package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.adapters.CategoriasAdapter
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.databinding.FragmentCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapter: CategoriasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentCategoriasBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         //RecyclerView e Adapter
        adapter = CategoriasAdapter(listaCategorias)

        binding.rvListaCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListaCategorias.adapter = adapter

        // Carrega categorias existentes
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                listaCategorias.clear()
                docs.forEach { doc ->
                    listaCategorias.add(Categoria(nome = doc.getString("nome") ?: "", id = doc.id))
                }
                adapter.notifyDataSetChanged() // informa mudanças
            }

        // Botão Add: insere item novo, rola e foca
        binding.btnAddCategoria.setOnClickListener {
            listaCategorias.add(Categoria(nome = "", id = null))
            val newPos = listaCategorias.lastIndex
            adapter.notifyItemInserted(newPos)
            binding.rvListaCategorias.scrollToPosition(newPos)
            adapter.entrarEmEdicao(newPos)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}