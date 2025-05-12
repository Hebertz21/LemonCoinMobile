import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lemoncoin.classeObjetos.Categorias
import com.example.lemoncoin.databinding.RecyclerViewListaCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasAdapter(private val lista: MutableList<Categorias>) :
    RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder>() {

    inner class CategoriasViewHolder(val binding: RecyclerViewListaCategoriasBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriasViewHolder {
        val binding = RecyclerViewListaCategoriasBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoriasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val categoria = lista[position]
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val categorias = db
            .collection("usuarios")
            .document(user?.uid.toString())
            .collection("categoria")
            .document(categoria.nome)
        Log.i(null, "Terminou a busca de categorias")

        val nomeCategorias = categorias.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val nomeCategorias = document.getString("nome")
                holder.binding.txtCategoria.text = nomeCategorias
            }
        }

    }

    override fun getItemCount(): Int = lista.size
}

