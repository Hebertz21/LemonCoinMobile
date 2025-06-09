package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.R
import com.example.lemoncoin.adapters.ListaDespesasAdapter
import com.example.lemoncoin.databinding.FragmentDespesasBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DespesasFragment : Fragment() {

    private var _binding: FragmentDespesasBinding? = null
    private val binding get() = _binding!! // Uso seguro do binding

    private var txtDataInicio : String? = null
    private var dataInicio : Date? = null

    private var txtDataFim : String? = null
    private var dataFim : Date? = null

    private var listaDespesas : MutableList<Movimentacao>? = null

    private var listenerMov : ListenerRegistration? = null

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDespesasBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.iconFiltro.performClick()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconFiltro.visibility = View.GONE

        binding.btnAddDespesas.setOnClickListener(){
            trocarFragment(AddDespesasFragment())
        }

        listaDespesas = movListener()

        binding.etDataInicio.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data:")
                .setTheme(R.style.datePicker)
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
                binding.etDataInicio.setText(dataFormatada)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        //função para quando o texto mudar
        binding.etDataInicio.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                try {
                    txtDataInicio = s.toString()
                    dataInicio = SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).parse(txtDataInicio!!)
                    //Toast.makeText(requireContext(), "Data de início: $dataInicio", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    txtDataInicio = null
                    dataInicio = null
                    Log.e("Erro", "Erro ao converter data: $e")
                }
                try {
                    if(dataInicio != null && dataFim != null) {
                        if(txtDataInicio == "") return
                        if(dataFim!!.after(dataInicio)) {
                            val listaFiltrada = listaDespesas!!.filter {
                                //necessário colocar um dia antes para o filtro incluir o dia da data início
                                val inicio = Calendar.getInstance().apply { time = dataInicio!! }
                                inicio.add(Calendar.DAY_OF_YEAR, -1)
                                val data_inicio = inicio.time

                                val fim = Calendar.getInstance().apply { time = dataFim!! }
                                fim.add(Calendar.DAY_OF_YEAR, 1)
                                val data_fim = fim.time

                                it.data.after(data_inicio) && it.data.before(data_fim)
                            }.toMutableList()
                            carregarFiltro(listaFiltrada)
                        } else {
                            Toast.makeText(requireContext(),
                                "A data de início deve ser antes da data de fim!!", Toast.LENGTH_SHORT).show()
                            listaDespesas = movListener() //tira o filtro
                            binding.etDataInicio.setText("")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erro: $e", Toast.LENGTH_SHORT).show()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etDataFim.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data:")
                .setTheme(R.style.datePicker)
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
                binding.etDataFim.setText(dataFormatada)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        //função para quando o texto mudar
        binding.etDataFim.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                try {
                    txtDataFim = s.toString()
                    dataFim = SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).parse(txtDataFim)
                    //Toast.makeText(requireContext(), "Data de fim: $dataFim", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    txtDataFim = null
                    dataFim = null
                    Log.e("Erro", "Erro ao converter data: $e")
                }
                try {
                    if(dataInicio != null && dataFim != null) {
                        if(txtDataFim == "") return
                        if(dataInicio!!.before(dataFim)) {
                            val listaFiltrada = listaDespesas!!.filter {
                                //necessário colocar um dia antes para o filtro incluir o dia da data início
                                val inicio = Calendar.getInstance().apply { time = dataInicio!! }
                                inicio.add(Calendar.DAY_OF_YEAR, -1)
                                val data_inicio = inicio.time

                                val fim = Calendar.getInstance().apply { time = dataFim!! }
                                fim.add(Calendar.DAY_OF_YEAR, 1)
                                val data_fim = fim.time

                                it.data.after(data_inicio) && it.data.before(data_fim)
                            }.toMutableList()
                            carregarFiltro(listaFiltrada)
                        } else {
                            Toast.makeText(requireContext(),
                                "A data de início deve ser antes da data de fim!!", Toast.LENGTH_SHORT).show()
                            listaDespesas = movListener() //tira o filtro
                            binding.etDataFim.setText("")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erro: $e", Toast.LENGTH_SHORT).show()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    private fun trocarFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerMovimentacoes, fragment)
            .addToBackStack(null) //
            .commit()
    }

    private fun movListener(): MutableList<Movimentacao> {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val dbMovimentacoes = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("movimentações")
        Log.i(null, "Terminou a busca de movimentações")


        val listaDespesas: MutableList<Movimentacao> = mutableListOf()
        listenerMov = dbMovimentacoes.orderBy("data")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("DespesasFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (_binding == null) { // <-- VERIFICAÇÃO CRUCIAL
                    Log.d("DespesasFragment", "Binding nulo em FragmentDespesas (snapshot), retornando.")
                    return@addSnapshotListener
                }

                listaDespesas.clear()
                if (snapshots != null) {
                    for (document in snapshots) {
                        Log.i(null, "entrou no ID: ${document.id}")
                        val categoriaId = document.get("categoriaId")?.toString()
                        val contaId = document.getString("contaId")
                        val data = document.getDate("data")
                        val nome = document.getString("nome")
                        val tipo = document.getString("tipo")
                        val valor = document.getDouble("valor")

                        if (categoriaId != null && contaId != null
                            && data != null && nome != null
                            && tipo != null && valor != null)
                        {
                            if (tipo == "Despesa") {
                                val movimentacao = Movimentacao(
                                    nome = nome,
                                    valor = valor,
                                    data = data,
                                    categoria = categoriaId,
                                    conta = contaId,
                                    tipo = tipo,
                                    id = document.id
                                )
                                listaDespesas.add(movimentacao)
                            }
                        } else {
                            Log.w("Firestore", "Documento com campos nulos: ${document.id}")
                        }
                    }
                    if(dataInicio != null && dataFim != null) {
                        val listaFiltrada = listaDespesas.filter {
                            //necessário colocar um dia antes para o filtro incluir o dia da data início
                            val inicio = Calendar.getInstance().apply { time = dataInicio!! }
                            inicio.add(Calendar.DAY_OF_YEAR, -1)
                            val data_inicio = inicio.time

                            val fim = Calendar.getInstance().apply { time = dataFim!! }
                            fim.add(Calendar.DAY_OF_YEAR, 1)
                            val data_fim = fim.time

                            it.data.after(data_inicio) && it.data.before(data_fim)
                        }.toMutableList()
                        carregarFiltro(listaFiltrada) //se tiver filtro ativo ele atuializa o filtro
                        return@addSnapshotListener
                    }
                    Log.i(null, "lista de movimentações: $listaDespesas")
                    val adapter = ListaDespesasAdapter(listaDespesas) { movimentacaoSelecionada ->
                        val fragment = AddDespesasFragment.newInstance(movimentacaoSelecionada.id)
                        trocarFragment(fragment)
                    }
                    binding.rvListaDespesas.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvListaDespesas.adapter = adapter

                    val valorTotal = listaDespesas.sumOf { it.valor }
                    val totalFormatado = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR")).format(valorTotal)
                    binding.txtValorTotal.text = totalFormatado
                }
            }

        return listaDespesas
    }

    private fun carregarFiltro(listaDespesas : MutableList<Movimentacao>) {
        Log.i(null, "lista de movimentações: $listaDespesas")
        val adapter = ListaDespesasAdapter(listaDespesas) { movimentacaoSelecionada ->
            val fragment = AddDespesasFragment.newInstance(movimentacaoSelecionada.id)
            trocarFragment(fragment)
        }
        binding.rvListaDespesas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListaDespesas.adapter = adapter

        val valorTotal = listaDespesas.sumOf { it.valor }
        val totalFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valorTotal)
        binding.txtValorTotal.text = totalFormatado

        binding.iconFiltro.visibility = View.VISIBLE
        binding.txtFiltro.visibility = View.GONE

        binding.iconFiltro.setOnClickListener {
            binding.etDataInicio.setText("")
            binding.etDataFim.setText("")

            binding.iconFiltro.visibility = View.GONE
            binding.txtFiltro.visibility = View.VISIBLE

            this.listaDespesas = movListener()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerMov?.remove() //Remove o listener para evitar vazamentos de memória e chamadas desnecessárias
        _binding = null // Evita vazamento de memória
    }

}