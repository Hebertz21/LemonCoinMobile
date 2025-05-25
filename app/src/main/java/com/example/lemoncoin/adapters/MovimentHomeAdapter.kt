package com.example.lemoncoin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.RecyclerViewMovimentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class MovimentHomeAdapter (
    private val lista: MutableList<Movimentacao>,
    private val onItemClick: () -> Unit
) : RecyclerView.Adapter<MovimentHomeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerViewMovimentHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val listaMovimentacoes : MutableList<Movimentacao> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewMovimentHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick()
        }
        val movimentacao = lista[position]

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
                            id = doc.id)
                    )
                }
                Log.i(null, "Lista de movimentações: ${listaMovimentacoes}")
            }
        val valor = movimentacao.valor

        val valorFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valor)

        holder.binding.txtSaldo.text = valorFormatado
        holder.binding.txtMovimentacao.text = movimentacao.nome
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}