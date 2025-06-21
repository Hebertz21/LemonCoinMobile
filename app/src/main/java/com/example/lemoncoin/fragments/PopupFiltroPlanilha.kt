package com.example.lemoncoin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.lemoncoin.R
import com.example.lemoncoin.databinding.PopupFiltroPlanilhaBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PopupFiltroPlanilha : DialogFragment() {
    private var _binding: PopupFiltroPlanilhaBinding? = null
    private val binding get() = _binding!!

    private var dataInicio: Date? = null
    private var txtDataInicio: String? = null
    private var dataFim: Date? = null
    private var txtDataFim: String? = null

    private var usarDespesas : Boolean = false
    private var usarReceitas : Boolean = false
    private var todasDatas : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupFiltroPlanilhaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }
        binding.btnExportar.setOnClickListener {
            exportar()
        }

        binding.etDataInicio.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data:")
                .setTheme(R.style.datePicker)
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
                binding.etDataInicio.setText(dataFormatada)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        binding.etDataInicio.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                try {
                    txtDataInicio = s.toString()
                    dataInicio = SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).parse(txtDataInicio!!)
                    //Toast.makeText(requireContext(), "Data de início: $dataInicio", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    txtDataInicio = null
                    dataInicio = null
                    Log.e("Erro", "Erro ao converter data: $e")
                }
                try {
                    if(dataInicio != null && dataFim != null) {
                        if(txtDataInicio == "") return
                        if(dataFim!!.before(dataInicio)) {
                            Toast.makeText(requireContext(),
                                "A data de início deve ser antes da data de fim!!", Toast.LENGTH_SHORT).show()
                            binding.etDataInicio.setText("")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erro: $e", Toast.LENGTH_SHORT).show()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etDataFim.setOnClickListener(){
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data:")
                .setTheme(R.style.datePicker)
                .build()

            datePicker.addOnPositiveButtonClickListener { millis ->
                val dataFormatada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
                binding.etDataFim.setText(dataFormatada)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        binding.etDataFim.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                try {
                    txtDataFim = s.toString()
                    dataFim = SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).parse(txtDataFim)
                    //Toast.makeText(requireContext(), "Data de fim: $dataFim", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    txtDataFim = null
                    dataFim = null
                    Log.e("Erro", "Erro ao converter data: $e")
                }
                try {
                    if(dataInicio != null && dataFim != null) {
                        if(txtDataFim == "") return
                        if(dataInicio!!.after(dataFim)) {
                            Toast.makeText(requireContext(),
                                "A data de início deve ser antes da data de fim!!", Toast.LENGTH_SHORT).show()
                            binding.etDataFim.setText("")
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erro: $e", Toast.LENGTH_SHORT).show()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.chkTodasDatas.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etDataInicio.isEnabled = false
                binding.etDataFim.isEnabled = false
                binding.etDataInicio.setText("")
                binding.etDataFim.setText("")
                dataInicio = null
                dataFim = null
                todasDatas = true
            } else {
                binding.etDataInicio.isEnabled = true
                binding.etDataFim.isEnabled = true
                todasDatas = false
            }
        }
        binding.switchDespesas.setOnCheckedChangeListener { _, isChecked ->
            usarDespesas = isChecked
            if(isChecked) {
                binding.switchDespesas.thumbTintList = resources.getColorStateList(R.color.switch_ligado, null)
            } else {
                binding.switchDespesas.thumbTintList = resources.getColorStateList(R.color.switch_desligado, null)
            }
        }

        binding.switchReceitas.setOnCheckedChangeListener { _, isChecked ->
            usarReceitas = isChecked
            if(isChecked) {
                binding.switchReceitas.thumbTintList = resources.getColorStateList(R.color.switch_ligado, null)
            } else {
                binding.switchReceitas.thumbTintList = resources.getColorStateList(R.color.switch_desligado, null)
            }
        }
    }

    fun exportar(){
        if((dataInicio == null || dataFim == null) && !todasDatas){
            Toast.makeText(requireContext(), "Preencha as datas", Toast.LENGTH_SHORT).show()
            return
        }
        if(!usarDespesas && !usarReceitas){
            Toast.makeText(requireContext(), "Selecione despesa ou receita para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle()
        bundle.putBoolean("usarDespesas", usarDespesas)
        bundle.putBoolean("usarReceitas", usarReceitas)
        bundle.putBoolean("todasDatas", todasDatas)
        bundle.putString("dataInicio", txtDataInicio)
        bundle.putString("dataFim", txtDataFim)
        setFragmentResult("filtroPlanilhaRequestKey", bundle)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}