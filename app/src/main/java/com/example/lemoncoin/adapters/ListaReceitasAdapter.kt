package com.example.lemoncoin.adapters

import android.app.AlertDialog
import android.widget.Toast
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.R
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.RecyclerViewListaMovimentacoesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.concurrent.Executors


class ListaReceitasAdapter(private val lista: MutableList<Movimentacao>,
                           private val onEditClick: (Movimentacao) -> Unit) :
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
        val movimentacao = lista[position]

        val valor = movimentacao.valor
        val valorFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valor)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val data = dateFormat.format(movimentacao.data)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        //busca de conta por id
        val conta = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("contas") //contas do usuário atual
            .document(movimentacao.conta)

        val nomeConta = conta.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val nomeConta = document.getString("nome")
                holder.binding.txtContaRvMovimentacao.text = nomeConta
            } else {
                holder.binding.txtContaRvMovimentacao.text = "Nenhuma conta vinculada"
            }
        }

        //busca de categoria por id
        val categoria = db
            .collection("usuarios") //pasta usuários
            .document(user?.uid.toString()) //pasta do usuário atual
            .collection("categorias") //categorias do usuário atual
            .document(movimentacao.categoria)

        val nomeCategoria = categoria.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val nomeCategoria = document.getString("nome")
                holder.binding.txtCategoriaRvMovimentacao.text = nomeCategoria
            } else {
                holder.binding.txtCategoriaRvMovimentacao.text = "Nenhuma categoria vinculada"
            }
        }

        holder.binding.txtNomeRvMovimentacao.text = movimentacao.nome
        holder.binding.txtValorRvMovimentacao.text = valorFormatado
        holder.binding.txtDataRvMovimentacao.text = data

        holder.binding.imgBtnEditar.setOnClickListener {
            onEditClick(movimentacao)
        }

        holder.binding.imgBtnDelete.setOnClickListener {
            //mensagem de confirmação
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Excluir receita")
                .setMessage("Deseja realmente excluir a receita ${movimentacao.nome} (${data})?")
                .setPositiveButton("Sim") { dialog, _ ->
                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        db.collection("usuarios")
                            .document(user?.uid.toString())
                            .collection("movimentações")
                            .document(movimentacao.id)
                            .delete()
                            .addOnSuccessListener {
                                val context = holder.itemView.context
                                Toast.makeText(context, "Receita excluída com sucesso",
                                    Toast.LENGTH_LONG).show()
                                lista.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, lista.size)
                            }
                            .addOnFailureListener { e ->
                                val context = holder.itemView.context
                                Toast.makeText(context, "Erro ao excluir receita: $e",
                                    Toast.LENGTH_LONG).show()
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()

            dialog.setOnShowListener {
                val btnSim = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val btnNao = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                btnSim.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textView))
                btnNao.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textView))

            }
            dialog.show()
            true
        }
    }

    override fun getItemCount(): Int = lista.size
}