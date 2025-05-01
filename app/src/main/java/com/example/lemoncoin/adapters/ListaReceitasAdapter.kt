package com.example.lemoncoin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.RvMovimentacoesClasse
import com.example.lemoncoin.databinding.RecyclerViewListaMovimentacoesBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat


class ListaReceitasAdapter(private val lista: List<RvMovimentacoesClasse>) :
    RecyclerView.Adapter<ListaReceitasAdapter.ReceitaViewHolder>() {
    inner class ReceitaViewHolder(val binding: RecyclerViewListaMovimentacoesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val binding = RecyclerViewListaMovimentacoesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReceitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val movimentacoes = lista[position]

        val valor = movimentacoes.valor
        val valorFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valor)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val data = dateFormat.format(movimentacoes.data)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        //busca de conta por id
        val conta = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("contas") //contas do usuário atual
            .document(movimentacoes.conta)

        val nomeConta = conta.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val nomeConta = document.getString("nome")
                holder.binding.txtContaRvMovimentacao.text = nomeConta
            } else {
                holder.binding.txtContaRvMovimentacao.text = ""
            }
        }

        //busca de categoria por id
        val categoria = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("categorias") //categorias do usuário atual
            .document(movimentacoes.categoria)

        val nomeCategoria = categoria.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val nomeCategoria = document.getString("nome")
                holder.binding.txtCategoriaRvMovimentacao.text = nomeCategoria
            } else {
                holder.binding.txtCategoriaRvMovimentacao.text = ""
            }
        }

        holder.binding.txtNomeRvMovimentacao.text = movimentacoes.nome
        holder.binding.txtValorRvMovimentacao.text = valorFormatado
        holder.binding.txtDataRvMovimentacao.text = data
    }

    override fun getItemCount(): Int = lista.size
}