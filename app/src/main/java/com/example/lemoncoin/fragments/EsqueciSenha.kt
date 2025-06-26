import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.EsqueciSenhaBinding
import com.example.lemoncoin.databinding.ListaContasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class EsqueciSenha : DialogFragment() {

    private var _binding: EsqueciSenhaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EsqueciSenhaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelarEmail.setOnClickListener(){
            dismiss()
        }

        binding.btnEnviarEmail.setOnClickListener(){
            val email = binding.InputEmailEsqueciSenha.text.toString()
            val auth = FirebaseAuth.getInstance()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Informe o email", Toast.LENGTH_SHORT).show()

            }else{
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener { task ->
                        Toast.makeText(requireContext(),
                            "E-mail de recuperação enviado com sucesso. Confira sua caixa de entrada.",
                            Toast.LENGTH_LONG).show()

                        Log.i("FirebaseTest", "Email enviado")
                        binding.InputEmailEsqueciSenha.text?.clear()
                    }.addOnFailureListener {
                        Log.i("FirebaseTest", "Erro ao enviar email")
                        Toast.makeText(requireContext(),
                            "E-mail não enviado. Verifique o E-mail e tente novamente.",
                            Toast.LENGTH_LONG).show()

                    }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

