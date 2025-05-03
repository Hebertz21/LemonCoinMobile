package com.example.lemoncoin.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Conta
import com.example.lemoncoin.databinding.RecyclerViewContaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.concurrent.Executors
import java.util.Locale

private val db = FirebaseFirestore.getInstance()
private val userId = FirebaseAuth.getInstance().currentUser
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
        val valor = conta.saldo
        val saldoFormatado = NumberFormat
            .getCurrencyInstance(Locale("pt", "BR")).format(valor)

        holder.binding.txtNomeConta.text = conta.nome
        holder.binding.txtSaldo.text = saldoFormatado
        holder.binding.imgConta.setImageResource(conta.iconeResId)

        //ao clicar no item
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "clicado: ${conta.nome}", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Excluir conta")
                .setMessage("Deseja realmente excluir a conta ${conta.nome}?")
                .setPositiveButton("Sim") { dialog, _ ->
                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        db.collection("usuarios")
                            .document(userId?.uid.toString())
                            .collection("contas")
                            .document(conta.id)
                            .delete()
                            .addOnSuccessListener { println("Conta excluída com sucesso") }
                            .addOnFailureListener { e -> println("Erro ao excluir conta: $e") }
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int = contas.size
}
