package com.example.lemoncoin.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.ClasseObjetos.Conta
import com.example.lemoncoin.databinding.RecyclerViewContaBinding

class ContaAdapter(private val contas: List<Conta>) :
    RecyclerView.Adapter<ContaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerViewContaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewContaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conta = contas[position]
        holder.binding.txtNomeConta.text = conta.nome
        holder.binding.txtSaldo.text = conta.saldo
        holder.binding.imgConta.setImageResource(conta.iconeResId)
    }

    override fun getItemCount(): Int = contas.size
}
