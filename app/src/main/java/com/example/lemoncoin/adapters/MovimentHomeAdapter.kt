package com.example.lemoncoin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.RecyclerViewMovimentHomeBinding
import java.text.NumberFormat
import java.util.Locale

class MovimentHomeAdapter (
    private val lista: MutableList<Movimentacao>,
    private val onItemClick: () -> Unit
) : RecyclerView.Adapter<MovimentHomeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerViewMovimentHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        val valor = movimentacao.valor

        val valorFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valor)

        if(valor < 0) holder.binding.txtSaldo.text = valorFormatado
        else holder.binding.txtSaldo.text = "+${valorFormatado}"
        holder.binding.txtMovimentacao.text = movimentacao.nome
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}