// CategoriasAdapter.kt
package com.example.lemoncoin.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.R
import com.example.lemoncoin.classeObjetos.Categoria
import com.example.lemoncoin.databinding.RecyclerViewListaCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class CategoriasAdapter(
    private val lista: MutableList<Categoria>
) : RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder>() {

    private val editingPositions = mutableSetOf<Int>() // posições em modo edição
    private val db  = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    inner class CategoriasViewHolder(val binding: RecyclerViewListaCategoriasBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val binding = RecyclerViewListaCategoriasBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoriasViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val categoria    = lista[position]
        val txtCategoria = holder.binding.txtCategoria
        val btnEditar    = holder.binding.imgBtnEditarCategoria
        val btnDeletar   = holder.binding.imgBtnDeleteCategoria

        if (editingPositions.contains(position)) {
            btnEditar.setImageResource(R.drawable.icon_done)
        } else {
            btnEditar.setImageResource(R.drawable.ic_pencil)
        }

        txtCategoria.imeOptions = EditorInfo.IME_ACTION_DONE
        txtCategoria.setSingleLine(true)
        txtCategoria.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                txtCategoria.clearFocus()  // dispara o OnFocusChangeListener
                true
            } else false
        }

        // Detecta novoItem e se está em modo edição
        val novoItem = categoria.id == null
        val editing  = editingPositions.contains(position) || novoItem

        //Preenche texto e habilita/desabilita
        txtCategoria.setText(categoria.nome)
        txtCategoria.isEnabled             = editing
        txtCategoria.isFocusable           = editing
        txtCategoria.isFocusableInTouchMode = editing

         //Se em edição, pede foco e abre teclado
        if (editing) {
            txtCategoria.post {
                txtCategoria.requestFocus()
                txtCategoria.setSelection(txtCategoria.text?.length ?: 0)
                val imm = txtCategoria.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(txtCategoria, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        //  Remove flag de edição automática em novoItem
        if (novoItem) editingPositions.remove(position)

        // Botão Editar ativa modo edição
        btnEditar.setOnClickListener {
            //click no v
            if (editingPositions.contains(position)) {
                //quando clica, chama a função de lost focus de txtCategoria
                holder.binding.txtCategoria.clearFocus()
            }
            else {
                editingPositions.add(position)
                notifyItemChanged(position)
            }
        }

        //Ao perder foco, salva e então remove edição
        txtCategoria.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val novoNome = txtCategoria.text.toString().trim()
                if (novoNome.isNotEmpty()) {
                    uid?.let { userId ->
                        val ref = db.collection("usuarios")
                            .document(userId)
                            .collection("categorias")

                        if (categoria.id == null) {
                            // ADD
                            ref.add(mapOf("nome" to novoNome))
                                .addOnSuccessListener { docRef ->
                                    //por conta do listener, não precisamos dessa parte do codigo
//                                    categoria.id = docRef.id
//                                    categoria.nome = novoNome
//                                    lista[position] = categoria
                                    Log.d("CAT_ADAPTER", "Criou ${docRef.id}")
                                    //remove do modo de edição
                                    editingPositions.remove(position)
                                    notifyItemChanged(position)
                                    //fecha o teclado
                                    val imm = txtCategoria.context
                                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(txtCategoria.windowToken, 0)
                                }
                        } else {
                            // UPDATE
                            ref.document(categoria.id!!)
                                .update("nome", novoNome)
                                .addOnSuccessListener {
//                                    categoria.nome = novoNome
//                                    lista[position] = categoria
                                    Log.d("CAT_ADAPTER", "Atualizou ${categoria.id}")
                                    editingPositions.remove(position)
                                    notifyItemChanged(position)
                                }
                        }
                    }
                }
            }
        }


        //Delete
        btnDeletar.setOnClickListener {
            if (categoria.id == null) {
                lista.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, lista.size)
                return@setOnClickListener
            } else if(editing){
                editingPositions.remove(position)
                notifyItemChanged(position)
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Excluir categoria")
                .setMessage("Deseja realmente excluir '${categoria.nome}'?")
                .setPositiveButton("Sim") { dialog, _ ->
                    categoria.id?.let { catId ->
                        db.collection("usuarios")
                            .document(uid.toString())
                            .collection("categorias")
                            .document(catId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Categoria excluída com sucesso",
                                    Toast.LENGTH_LONG
                                ).show()
                                //por conta do listener, não precisamos remover por aqui
//                                lista.removeAt(position)
//                                notifyItemRemoved(position)
//                                notifyItemRangeChanged(position, lista.size)
                            }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Não") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textView))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textView))
            }
            dialog.show()
        }
    }
    fun entrarEmEdicao(posicao: Int) {
        editingPositions.add(posicao)
        notifyItemChanged(posicao)
    }

}
