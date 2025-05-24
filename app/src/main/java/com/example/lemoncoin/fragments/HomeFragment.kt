package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.CategoriaHomeAdapter
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapter : CategoriaHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarCategorias()

        binding.btnAddDespesaHome.setOnClickListener(){
            trocarFragment(AddDespesasFragment())
        }

        binding.btnAddReceitaHome.setOnClickListener(){
            trocarFragment(AddReceitasFragment())
        }


    }
    private fun trocarFragment(fragment: androidx.fragment.app.Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun carregarCategorias() {
        if (!::adapter.isInitialized) { // Verifica se o adapter já foi inicializado
            adapter = CategoriaHomeAdapter(listaCategorias)
            binding.RvCategorias.layoutManager = LinearLayoutManager(requireContext())
            binding.RvCategorias.adapter = adapter
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Lidar com o caso de usuário não logado, talvez limpar a lista e notificar
            listaCategorias.clear()
            if (::adapter.isInitialized) { // Verifica antes de notificar
                adapter.notifyDataSetChanged()
            }
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .collection("categorias")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { docs ->
                listaCategorias.clear() // Limpa a lista antes de adicionar novos itens
                docs.forEach { doc ->
                    listaCategorias.add(Categoria(nome = doc.getString("nome") ?: "", id = doc.id))
                }
                // Notifica o adapter que o conjunto de dados foi alterado
                // Isso fará com que o RecyclerView se redesenhe com os novos dados
                if (::adapter.isInitialized) { // Verifica antes de notificar
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Erro ao carregar categorias", exception)
                listaCategorias.clear()
                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                }
            }

        Log.i(null, "Lista de categorias: ${listaCategorias}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }
}