import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.ListaContasBinding
import com.example.lemoncoin.databinding.ListaContasGenericasBinding

class ListaContasGenericas : DialogFragment() {

    private var _binding: ListaContasGenericasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListaContasGenericasBinding.inflate(inflater, container, false)

        fun selectConta(imgResId: Int, txtConta: String) {
            if (txtConta.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha o nome da conta",
                    Toast.LENGTH_SHORT).show()
                return
            }
            val bundle = bundleOf("imgResId" to imgResId, "txtConta" to txtConta)
            setFragmentResult("requestKey", bundle)
            dismiss()
        }

        binding.linearGenerico1.setOnClickListener {
            selectConta(R.drawable.generico_1, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico2.setOnClickListener {
            selectConta(R.drawable.generico_2, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico3.setOnClickListener {
            selectConta(R.drawable.generico_3, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico4.setOnClickListener {
            selectConta(R.drawable.generico_4, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico5.setOnClickListener {
            selectConta(R.drawable.generico_5, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico6.setOnClickListener {
            selectConta(R.drawable.generico_6, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico7.setOnClickListener {
            selectConta(R.drawable.generico_7, binding.InputNomeBanco.text.toString())
        }
        binding.linearGenerico8.setOnClickListener {
            selectConta(R.drawable.generico_8, binding.InputNomeBanco.text.toString())
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

