package com.example.lemoncoin.classeObjetos

import java.util.Date

data class RvMovimentacoesClasse(
    val nome: String,
    val valor: Double,
    val data: Date,
    val categoria: String,
    val conta: String,
    val tipo: String = ""
)
