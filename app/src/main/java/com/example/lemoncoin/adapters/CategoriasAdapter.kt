package com.example.lemoncoin.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Categorias
import com.example.lemoncoin.databinding.RecyclerViewListaCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasAdapter(
    private val lista: MutableList<Categorias>
) : RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder>() {


    private val editingPositions = mutableSetOf<Int>() //Posições em modo de edição

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    inner class CategoriasViewHolder(val binding: RecyclerViewListaCategoriasBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val binding = RecyclerViewListaCategoriasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoriasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val categoria = lista[position]
        val txtCategorias = holder.binding.txtCategoria
        val btnEditar = holder.binding.imgBtnEditarCategoria

        val novoItem = categoria.id == null

        txtCategorias.setText(categoria.nome)
        val editing = editingPositions.contains(position)
        txtCategorias.isEnabled = editing
        txtCategorias.isFocusable = editing
        txtCategorias.isFocusableInTouchMode = editing


        btnEditar.setOnClickListener {
            if (editingPositions.add(position)) {
                notifyItemChanged(position)
                // pede foco e mostra teclado
                txtCategorias.post {
                    txtCategorias.requestFocus()
                    val imm = txtCategorias.context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(txtCategorias, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        txtCategorias.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && editingPositions.contains(position)) {
                val novoNome = txtCategorias.text.toString().trim()
                if (novoNome.isNotEmpty()) {
                    uid?.let { userId ->
                        val ref = db.collection("usuarios")
                            .document(userId)
                            .collection("categorias")
                        if (categoria.id != null) {
                            // UPDATE existente
                            ref.document(categoria.id!!)
                                .update("nome", novoNome)
                                .addOnSuccessListener {
                                    Log.d("CAT_ADAPTER", "Atualizou ${categoria.id} → $novoNome")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CAT_ADAPTER", "Erro no update", e)
                                }
                        } else {
                            //Adiciona o novo nome no BD
                            ref.add(mapOf("nome" to novoNome))
                                .addOnSuccessListener { docRef ->
                                    Log.d("CAT_ADAPTER", "Criou categoria ${docRef.id}")
                                    // guarda o id no objeto local
                                    categoria.id = docRef.id
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CAT_ADAPTER", "Erro no add", e)
                                }
                        }
                        categoria.nome = novoNome
                        lista[holder.adapterPosition] = categoria
                    }
                }
                editingPositions.remove(position)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = lista.size
}
