package com.example.lemoncoin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.RecyclerViewCategoriaHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Date
import java.util.Locale


class CategoriaHomeAdapter(
    private val lista: MutableList<Categoria>,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<CategoriaHomeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerViewCategoriaHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val listaMovimentacoes : MutableList<Movimentacao> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewCategoriaHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = lista[position]
        var saldo = 0.0

        //puxa todas movimentações do usuário
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .collection("movimentações")
            .orderBy("data")
            .get()
            .addOnSuccessListener { docs ->
                listaMovimentacoes.clear()
                docs.forEach { doc ->
                    val categoriaId = doc.get("categoriaId")?.toString()
                    listaMovimentacoes.add(
                        Movimentacao(
                            nome = doc.getString("nome") ?: "",
                            valor = doc.getDouble("valor") ?: 0.0,
                            data = doc.getDate("data") ?: Date(),
                            tipo = doc.getString("tipo") ?: "",
                            categoria = categoriaId ?: "",
                            conta = doc.getString("contaId") ?: "",
                            id = doc.id
                        )
                    )
                }
                Log.i(null, "Lista de movimentações: ${listaMovimentacoes}")
                for (movimentacao in listaMovimentacoes) {
                    if (movimentacao.categoria == categoria.id.toString()) {
                        Log.i(null, "entrou no if")
                        if (movimentacao.tipo == "Despesa") {
                            saldo -= movimentacao.valor
                        } else if (movimentacao.tipo == "Receita") {
                            saldo += movimentacao.valor
                        }
                    }
                }

                val saldoFormatado = NumberFormat
                    .getCurrencyInstance(Locale("pt", "BR")).format(saldo)

                holder.binding.txtSaldo.text = saldoFormatado
            }


        holder.binding.txtCategoria.text = categoria.nome

        holder.binding.containerCategoria.setOnClickListener {
            onClick()
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}


