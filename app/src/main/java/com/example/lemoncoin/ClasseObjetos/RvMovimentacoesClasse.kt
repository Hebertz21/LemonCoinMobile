package com.example.lemoncoin.ClasseObjetos

data class RvMovimentacoesClasse(
    val nome: String,
    val valor: Double,
    val data: String,
    val categoria: String,
    val conta: String,
    val tipo: String = ""
)
