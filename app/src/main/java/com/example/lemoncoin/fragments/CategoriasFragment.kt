package com.example.lemoncoin.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.adapters.CategoriasAdapter
import com.example.lemoncoin.classeObjetos.Categorias
import com.example.lemoncoin.databinding.FragmentCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categorias>()
    private lateinit var adapter: CategoriasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentCategoriasBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoriasAdapter(listaCategorias)
        binding.rvListaCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListaCategorias.adapter = adapter

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
                    val nome = doc.getString("nome") ?: ""
                    listaCategorias.add(Categorias(nome = nome, id = doc.id))
                }
                adapter.notifyDataSetChanged() //Informa o RV que houve mudança
            }


        binding.btnAddCategoria.setOnClickListener {
            listaCategorias.add(Categorias(nome = "", id = null))
            val newPos = listaCategorias.lastIndex
            adapter.notifyItemInserted(newPos)

            // rola e foca no EditText do novo item:
            binding.rvListaCategorias.postDelayed({
                binding.rvListaCategorias.scrollToPosition(newPos)
                val vh = binding.rvListaCategorias
                    .findViewHolderForAdapterPosition(newPos)
                        as? CategoriasAdapter.CategoriasViewHolder
                vh?.binding?.txtCategoria?.let { edit ->
                    edit.requestFocus()
                    val imm = edit.context
                        .getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
                }
            }, 200)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
