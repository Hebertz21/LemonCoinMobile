import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.ListaContasBinding

class ListaContas : DialogFragment() {

    private var _binding: ListaContasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListaContasBinding.inflate(inflater, container, false)

        fun selectConta(imgResId: Int, txtConta: String) {
            val bundle = bundleOf("imgResId" to imgResId, "txtConta" to txtConta)
            setFragmentResult("requestKey", bundle)
            dismiss()
        }

        binding.linearNubank.setOnClickListener {
            selectConta(R.drawable.nubank, binding.txtViewNubank.text.toString())
        }
        binding.linearBB.setOnClickListener {
            selectConta(R.drawable.banco_do_brasil, binding.txtViewBB.text.toString())
        }
        binding.linearBradesco.setOnClickListener {
            selectConta(R.drawable.bradesco, binding.txtViewBradesco.text.toString())
        }
        binding.linearCaixa.setOnClickListener {
            selectConta(R.drawable.caixa, binding.txtViewCaixa.text.toString())
        }
        binding.linearItau.setOnClickListener {
            selectConta(R.drawable.itau, binding.txtViewItau.text.toString())
        }
        binding.linearSantander.setOnClickListener {
            selectConta(R.drawable.santander, binding.txtViewSantander.text.toString())
        }
        binding.linearPicpay.setOnClickListener {
            selectConta(R.drawable.picpay, binding.txtViewPicpay.text.toString())
        }
        binding.linearSicredi.setOnClickListener {
            selectConta(R.drawable.sicredi, binding.txtViewSicredi.text.toString())
        }
        binding.linearMercadoPago.setOnClickListener {
            selectConta(R.drawable.mercado_pago, binding.txtViewMercadoPago.text.toString())
        }
        binding.linearInter.setOnClickListener {
            selectConta(R.drawable.inter, binding.txtViewInter.text.toString())
        }
        binding.linearStone.setOnClickListener {
            selectConta(R.drawable.stone, binding.txtViewStone.text.toString())
        }
        binding.linearWise.setOnClickListener {
            selectConta(R.drawable.wise, binding.txtViewWise.text.toString())
        }
        binding.linearVivo.setOnClickListener {
            selectConta(R.drawable.vivo, binding.txtViewVivo.text.toString())
        }
        binding.linearCofre.setOnClickListener {
            selectConta(R.drawable.cofre, binding.txtViewCofre.text.toString())
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

