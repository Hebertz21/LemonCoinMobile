import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.lemoncoin.databinding.DialogSairBinding

class ConfirmExitDialogFragment : DialogFragment() {

    private var _binding: DialogSairBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSairBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancel.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "exitRequestKey",
                bundleOf("confirmedExit" to false)
            )
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "exitRequestKey",
                bundleOf("confirmedExit" to true)
            )
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
