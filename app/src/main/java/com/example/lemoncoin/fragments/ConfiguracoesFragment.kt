package com.example.lemoncoin.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lemoncoin.HomeActivity
import com.example.lemoncoin.LoginActivity
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentConfiguracoesBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class ConfiguracoesFragment : Fragment() {
    private var _binding: FragmentConfiguracoesBinding? = null
    private val binding get() = _binding!!

    val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val db = FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(userId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfiguracoesBinding.inflate(inflater,
            container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //configurar cor dos botões radio
        binding.radio30Dias.buttonTintList = resources.getColorStateList(R.color.switch_ligado, null)
        binding.radioDia1.buttonTintList = resources.getColorStateList(R.color.switch_ligado, null)

        var opcCriarMov : Boolean
        var opcTipoMov : Int
        var nomeUser : String

        db.get().addOnSuccessListener {
            opcCriarMov = it.getBoolean("criarMovimentacao") ?: true
            opcTipoMov = it.getLong("tipoMovHome")?.toInt() ?: 1
            nomeUser = it.getString("nome") ?: ""

            if(opcCriarMov) {
                corSwitch(true)
                binding.switchCriarMov.isChecked = true
            } else {
                corSwitch(false)
                binding.switchCriarMov.isChecked = false
            }

            if(opcTipoMov == 1) {
                binding.radio30Dias.isChecked = true
            } else {
                binding.radioDia1.isChecked = true
            }

            binding.txtNomeUsuario.text = nomeUser
        }

        binding.switchCriarMov.setOnClickListener(){
            if(binding.switchCriarMov.isChecked){
                corSwitch(true)
                db.update("criarMovimentacao", true)
            } else {
                corSwitch(false)
                db.update("criarMovimentacao", false)
            }
        }

        binding.radio30Dias.setOnClickListener(){
            db.update("tipoMovHome", 1)
        }
        binding.radioDia1.setOnClickListener(){
            db.update("tipoMovHome", 2)
        }

        binding.btnDelConta.setOnClickListener(){
            val user = FirebaseAuth.getInstance().currentUser
            fun deletar() {
                AlertDialog.Builder(requireContext())
                    .setTitle("Deletar Conta ⚠️")
                    .setMessage("Está ciente de que esta ação é irreversível?")
                    .setPositiveButton("Sim, estou ciente") { _, _ ->
                        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            //db.delete().await()
                            user?.delete()?.await()
                            Firebase.auth.signOut()
                            Toast.makeText(requireContext(), "Conta deletada com sucesso!",
                                Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent)
                            (activity as? HomeActivity)?.finish()
                        }
                    }
                    .setNegativeButton("Não, cancelar", null)
                    .show()
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Deletar Conta ⚠\uFE0F")
                .setMessage("Você tem certeza que deseja deletar sua conta? Esta ação é irreversível.")
                .setPositiveButton("Sim") { _, _ ->
                    deletar()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }


    private fun corSwitch(isChecked: Boolean) {
        if (isChecked) {
            binding.switchCriarMov.thumbTintList =
                resources.getColorStateList(R.color.switch_ligado, null)
        } else {
            binding.switchCriarMov.thumbTintList =
                resources.getColorStateList(R.color.switch_desligado, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}