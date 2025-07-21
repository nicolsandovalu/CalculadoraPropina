package com.example.calculadorapropinas.utils

object CalculadoraUtils {

    fun calcularPropina(monto: Double, porcentaje: Double): Double {
        return monto * porcentaje
    }

    fun esNumeroValido(valor: String?): Boolean{
        return !valor.isNullOrEmpty() && valor.toDoubleOrNull() != null && valor.toDouble() >= 0
    }
}