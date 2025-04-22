package com.example.lemoncoin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.FragmentAddDespesasBinding
import com.example.lemoncoin.databinding.FragmentAddReceitasBinding
import com.example.lemoncoin.databinding.FragmentDespesasBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddDespesasFragment : Fragment() {

    private var _binding: FragmentAddDespesasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(  //Metodo que constroi a visualização do fragment
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            _binding = FragmentAddDespesasBinding.inflate(inflater,
                container, false)
        binding.etDataDespesa.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setTheme(R.style.datePicker)
                .build()
            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataformatada = SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault()).format(Date(millis))
                binding.etDataDespesa.setText(dataformatada)
            }
            datePicker.show(parentFragmentManager, "Date_Picker")
        }
        return binding.root
    }
}