package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ListenerRegistration

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapter: CategoriasAdapter

    private var categoriasListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentCategoriasBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenerCategorias()

        // Botão Add: insere item novo, rola e foca
        binding.btnAddCategoria.setOnClickListener {
            listaCategorias.add(Categoria(nome = "", id = null))
            val newPos = listaCategorias.lastIndex
            adapter.notifyItemInserted(newPos)
            binding.rvListaCategorias.scrollToPosition(newPos)
            adapter.entrarEmEdicao(newPos)
        }

    }

    private fun listenerCategorias() {
        // Carrega categorias existentes
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbCategorias = FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("categorias")

        categoriasListener = dbCategorias.orderBy("nome")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("CategoriasFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("CategoriasFragment", "Binding nulo em FragmentCategorias (snapshot), retornando.")
                    return@addSnapshotListener
                }

                if(snapshots != null) {
                    listaCategorias.clear()
                    snapshots.forEach { doc ->
                        listaCategorias.add(Categoria(
                            nome = doc.getString("nome") ?: "",
                            id = doc.id
                        ))
                    }
                    adapter = CategoriasAdapter(listaCategorias, object : CategoriasAdapter.OnCategoriaActionListener {
                        override fun mostrarBtnAdd(mostrar : Boolean){
                            if (_binding != null) {
                                if(mostrar){
                                    binding.btnAddCategoria.visibility = View.VISIBLE
                                    binding.btnAddCategoria.isEnabled = true
                                } else {
                                    binding.btnAddCategoria.visibility = View.INVISIBLE
                                    binding.btnAddCategoria.isEnabled = false
                                }
                            } else {
                                Log.e("CategoriasFragment", "Binding é nulo em onMostrarBtnAdd.")
                            }
                        }
                    })
                    binding.rvListaCategorias.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvListaCategorias.adapter = adapter
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoriasListener?.remove() //Remove o listener para evitar vazamentos de memória e chamadas desnecessárias
        _binding = null
    }
}