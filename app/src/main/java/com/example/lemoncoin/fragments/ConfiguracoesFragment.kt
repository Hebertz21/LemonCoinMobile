package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentConfiguracoesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        var opcCriarMov : Boolean
        var opcTipoMov : Int

        db.get().addOnSuccessListener {
            opcCriarMov = it.getBoolean("criarMovimentacao") ?: true
            opcTipoMov = it.getLong("tipoMovHome")?.toInt() ?: 1

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