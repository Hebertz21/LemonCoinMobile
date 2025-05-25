package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.AtualizarContaFragment
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.CategoriaHomeAdapter
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.classeObjetos.Conta
import com.example.lemoncoin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapter : CategoriaHomeAdapter

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

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

        try {
            carregarCategorias()
            carregarContas()
        } catch (e: Exception) {
            Log.e("HomeFragment", "Erro ao carregar home", e)
        }

        binding.btnAddDespesaHome.setOnClickListener(){
            trocarFragment(AddDespesasFragment())
        }

        binding.btnAddReceitaHome.setOnClickListener(){
            trocarFragment(AddReceitasFragment())
        }

        binding.containerVerMais.setOnClickListener(){
            trocarFragment(ContasFragment())
        }

    }

    private fun trocarFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun carregarContas() {

        val listaContas = mutableListOf<Conta>()

        if(userId == null) {
            listaContas.clear()
            if (_binding != null) { // Verifica se binding ainda é válido
                binding.txtConta1.text = "N/A"
                binding.txtSaldo1.text = "R$ 0,00"
                binding.txtConta2.text = "N/A"
                binding.txtSaldo2.text = "R$ 0,00"
                // Desabilitar cliques se necessário
                binding.containerConta1.setOnClickListener(null)
                binding.containerConta2.setOnClickListener(null)
            }
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId ?: "")
            .collection("contas")
            .orderBy("saldo",
                com.google.firebase.firestore.Query.Direction.DESCENDING)
            // Para pegar as de maior saldo
            .limit(2) // Pega no máximo 2 contas
            .get()
            .addOnSuccessListener { docs ->
                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("HomeFragment", "Binding nulo em carregarContas (success), retornando.")
                    return@addOnSuccessListener
                }
                listaContas.clear()
                docs.forEach { doc ->
                    listaContas.add(Conta(
                        nome = doc.getString("nome") ?: "",
                        saldo = doc.getDouble("saldo") ?: 0.0,
                        id = doc.id,
                        iconeResId = R.drawable.ic_launcher_foreground)
                    )
                }
                Log.i(null, "Lista de contas: ${listaContas}")

                if(listaContas.size == 0) {
                    binding.containerConta1.visibility = View.GONE
                    binding.containerConta2.visibility = View.GONE
                    binding.txtVerMais.text = "Clique aqui para adcionar contas"
                    binding.containerVerMais.setOnClickListener(){
                        trocarFragment(AddContasFragment())
                    }
                    return@addOnSuccessListener
                }

                val saldo1 = NumberFormat
                    .getCurrencyInstance(Locale("pt", "BR"))
                    .format(listaContas[0].saldo)

                binding.txtConta1.text = listaContas[0].nome
                binding.txtSaldo1.text = saldo1

                binding.containerConta1.setOnClickListener(){
                    val contaSelecionada = listaContas[0]
                    val fragment = AtualizarContaFragment.newInstance(contaSelecionada.id)
                    trocarFragment(fragment)
                }

                if(listaContas.size > 1) {
                    val saldo2 = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR"))
                        .format(listaContas[1].saldo)

                    binding.txtConta2.text = listaContas[1].nome
                    binding.txtSaldo2.text = saldo2

                    binding.containerConta2.setOnClickListener() {
                        val contaSelecionada = listaContas[1]
                        val fragment = AtualizarContaFragment.newInstance(contaSelecionada.id)
                        trocarFragment(fragment)
                    }
                } else {
                    binding.containerConta2.visibility = View.GONE
                    binding.txtVerMais.text = "Clique aqui para adcionar contas"
                    binding.containerVerMais.setOnClickListener(){
                        trocarFragment(AddContasFragment())
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (_binding == null) {
                    Log.d("HomeFragment", "Binding nulo em carregarContas (failure), retornando.")
                    return@addOnFailureListener
                }
                Log.e("HomeFragment", "Erro ao carregar contas", exception)
                binding.txtConta1.text = "Erro"
                binding.txtSaldo1.text = ""
                binding.txtConta2.text = "Erro"
                binding.txtSaldo2.text = ""
            }

    }

    private fun carregarCategorias() {
        adapter = CategoriaHomeAdapter(listaCategorias)
        binding.RvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.RvCategorias.adapter = adapter

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
            .addOnFailureListener {
                Log.e("HomeFragment", "Erro ao carregar categorias", it)
            }

        Log.i(null, "Lista de categorias: ${listaCategorias}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita vazamento de memória
    }
}