package com.example.lemoncoin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.ClasseObjetos.RvMovimentacoesClasse
import com.example.lemoncoin.databinding.RecyclerViewListaMovimentacoesBinding


//lista: List<RvMovimentacoesClasse>: é a lista de dados que o RecyclerView vai exibir. Cada item é
// uma movimentação (despesa, receita)
//O viewHolder é responsavel por exibir cada uma individualemente

class ListaDespesasAdapter(private val lista: List<RvMovimentacoesClasse>) :
    RecyclerView.Adapter<ListaDespesasAdapter.DespesaViewHolder>() {

    inner class DespesaViewHolder(val binding: RecyclerViewListaMovimentacoesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DespesaViewHolder {
        val binding = RecyclerViewListaMovimentacoesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DespesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DespesaViewHolder, position: Int) {
        val movimentacoes = lista[position]
        //Cria a lista despesa
        //Holder associa cada item ao seu campo
        //Uma coisa é uma coisa, outra coisa é outra coisa
        holder.binding.txtNomeRvMovimentacao.text = movimentacoes.nome
        holder.binding.txtValorRvMovimentacao.text = "R$ ${movimentacoes.valor}"
        holder.binding.txtDataRvMovimentacao.text = movimentacoes.data
        holder.binding.txtCategoriaRvMovimentacao.text = movimentacoes.categoria
        holder.binding.txtContaRvMovimentacao.text = movimentacoes.conta
    }

    override fun getItemCount(): Int = lista.size
}
