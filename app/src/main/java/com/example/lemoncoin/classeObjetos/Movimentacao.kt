package com.example.lemoncoin.classeObjetos

import java.util.Date

data class Movimentacao(
    val nome: String,
    val valor: Double,
    val data: Date,
    val categoria: String,
    val conta: String,
    val tipo: String = "",
    val id: String,
    var nomeConta: String? = null,
    var nomeCategoria: String? = null

)
