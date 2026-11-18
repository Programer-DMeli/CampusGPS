package com.carbajal.campusgps.model

data class Posicion(val latitud: Double, val longitud: Double)

data class Lugar(
    val id: Int,
    val nombre: String,
    val categoria: String = "",
    val descripcion: String = "",
    val posicion: Posicion
)

data class LugarConDistancia(
    val lugar: Lugar,
    val distanciaMetros: Int = 0
)
