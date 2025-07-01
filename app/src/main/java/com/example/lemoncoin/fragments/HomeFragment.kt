package com.example.lemoncoin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.AtualizarContaFragment
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.CategoriaHomeAdapter
import com.example.lemoncoin.adapters.MovimentHomeAdapter
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.classeObjetos.Conta
import com.example.lemoncoin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val listaCategorias = mutableListOf<Categoria>()
    private lateinit var adapterCategorias : CategoriaHomeAdapter

    private val listaMovimentacoes = mutableListOf<Movimentacao>()
    private lateinit var adapterMovimentacoes : MovimentHomeAdapter

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private var listerConta: ListenerRegistration? = null
    private var listenerMov: ListenerRegistration? = null
    private var listenerCat: ListenerRegistration? = null
    private var dia1mes : Date? = null
    private var ultimos30Dias : Date? = null
    private var opcMov : Int? = null


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

        //a partir de que data aparece as movimentações
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId ?: "")
            .get()
            .addOnSuccessListener {
                // VERIFICAÇÃO CRUCIAL
                if (_binding == null) {
                    Log.d("HomeFragment", "Binding nulo no success listener inicial, retornando.")
                    return@addOnSuccessListener
                }

                opcMov = it.getLong("tipoMovHome")?.toInt() ?: 1

                val calendario = Calendar.getInstance()
                if (opcMov == 1) {
                    //calcula o dia atual -30
                    calendario.add(Calendar.DAY_OF_MONTH, -31)
                    ultimos30Dias = calendario.time

                    //uso para visualização do txt de data
                    calendario.add(Calendar.DAY_OF_MONTH, 1)
                } else {
                    // Calcula o dia 1 do mês atual para filtro
                    calendario.set(Calendar.DAY_OF_MONTH, 0)
                    dia1mes = calendario.time

                    //uso para visualização do txt de data
                    calendario.set(Calendar.DAY_OF_MONTH, 1)
                    calendario.add(Calendar.MONTH, 1)
                }

                val diaFormatado = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale("pt", "BR")).format(calendario.time)
                binding.txtCatInicio.text = "Valores a partir de: ${diaFormatado}"
                binding.txtMovInicio.text = "Valores a partir de: ${diaFormatado}"

                binding.txtSemMovimentacao.visibility = View.GONE
                binding.txtSemCategoria.visibility = View.GONE

                carregarCategorias()
                carregarContas()
                carregarMovimentacoes()
            }
            .addOnFailureListener{
                if (_binding == null) return@addOnFailureListener
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

        binding.constMovimentancoes.setOnClickListener(){
            trocarFragment(MovimentacoesFragment())
        }

    }

    private fun trocarFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarMovimentacoes() {
        adapterMovimentacoes = MovimentHomeAdapter(listaMovimentacoes) {
            trocarFragment(MovimentacoesFragment())
        }
        binding.rvMovimentacoesHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovimentacoesHome.adapter = adapterMovimentacoes

        if (userId == null) {
            listaMovimentacoes.clear()
            if (::adapterMovimentacoes.isInitialized) {
                adapterMovimentacoes.notifyDataSetChanged()
            }
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .collection("movimentações")
            .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { docs, e ->
                if(e != null) {
                    Log.w("HomeFragment", "Listen movimentações falhou.", e)
                    return@addSnapshotListener
                }

                if(_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("HomeFragment", "Binding nulo em carregarMovimentações (snapshot), retornando.")
                    return@addSnapshotListener
                }

                listaMovimentacoes.clear()

                docs?.forEach { doc ->

                    try {
                        if (opcMov == 2) {
                            if (doc.getDate("data")!!.before(dia1mes)) return@forEach
                        } else {
                            if (doc.getDate("data")!!.before(ultimos30Dias)) return@forEach
                        }
                    } catch (e: TypeNotPresentException) {
                        Log.e("Erro", "Erro ao converter data: $e")
                    }

                    var valor : Double
                    if (doc.getString("tipo") == "Despesa"){
                        valor = doc.getDouble("valor") ?: 0.0
                        valor *= -1
                    } else {
                        valor = doc.getDouble("valor") ?: 0.0
                    }
                    listaMovimentacoes.add(Movimentacao(
                        nome = doc.getString("nome") ?: "",
                        valor = valor ?: 0.0,
                        data = doc.getDate("data") ?: Date(),
                        categoria = doc.get("categoriaId").toString(),
                        conta = doc.getString("contaId") ?: "",
                        tipo = doc.getString("tipo") ?: "",
                        id = doc.id
                    ))
                }
                if (::adapterMovimentacoes.isInitialized) adapterMovimentacoes.notifyDataSetChanged()
                Log.i(null, "115: Lista de movimentações: ${listaMovimentacoes}")

                if(listaMovimentacoes.isEmpty()){
                    binding.txtSemMovimentacao.visibility = View.VISIBLE
                    binding.txtSemMovimentacao.setOnClickListener(){
                        trocarFragment(MovimentacoesFragment())
                    }
                }
                carregarCategorias()
            }
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
            .addSnapshotListener { docs, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen contas falhou.", e)
                    return@addSnapshotListener
                }

                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("HomeFragment", "Binding nulo em carregarContas (success), retornando.")
                    return@addSnapshotListener
                }

                listaContas.clear()
                docs?.forEach { doc ->
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
                    binding.txtVerMais.text = "Clique aqui para adicionar contas"
                    binding.containerVerMais.setOnClickListener(){
                        trocarFragment(AddContasFragment())
                    }
                    return@addSnapshotListener
                }

                binding.containerConta1.visibility = View.VISIBLE

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
                    binding.containerConta2.visibility = View.VISIBLE
                    binding.txtVerMais.text = "Ver Mais..."
                    binding.containerVerMais.setOnClickListener(){
                        trocarFragment(ContasFragment())
                    }

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
                    binding.txtVerMais.text = "Clique aqui para adicionar contas"
                    binding.containerVerMais.setOnClickListener(){
                        trocarFragment(AddContasFragment())
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarCategorias() {
        adapterCategorias = CategoriaHomeAdapter(listaCategorias, opcMov) {
            trocarFragment(CategoriasFragment())
        }
        binding.RvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.RvCategorias.adapter = adapterCategorias

        if (userId == null) {
            // Lidar com o caso de usuário não logado, talvez limpar a lista e notificar
            listaCategorias.clear()
            if (::adapterCategorias.isInitialized) { // Verifica antes de notificar
                adapterCategorias.notifyDataSetChanged()
            }
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId ?: "")
            .collection("categorias")
            .orderBy("nome")
            .addSnapshotListener { docs, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen categorias falhou.", e)
                    return@addSnapshotListener
                }

                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("HomeFragment", "Binding nulo em carregarCategorias (success), retornando.")
                    return@addSnapshotListener
                }

                listaCategorias.clear() // Limpa a lista antes de adicionar novos itens
                docs?.forEach { doc ->
                    listaCategorias.add(Categoria(nome = doc.getString("nome") ?: "", id = doc.id))
                }
                // Notifica o adapter que o conjunto de dados foi alterado
                // Isso fará com que o RecyclerView se redesenhe com os novos dados
                if (::adapterCategorias.isInitialized) { // Verifica antes de notificar
                    adapterCategorias.notifyDataSetChanged()
                }
                if(listaCategorias.isEmpty()){
                    binding.txtSemCategoria.visibility = View.VISIBLE
                    binding.txtSemCategoria.setOnClickListener(){
                        trocarFragment(CategoriasFragment())
                    }
                }
            }
        Log.i(null, "Lista de categorias: ${listaCategorias}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //evitar vazamento de memória
        listerConta?.remove()
        listenerMov?.remove()
        listenerCat?.remove()
        _binding = null // Evita vazamento de memória
    }
}