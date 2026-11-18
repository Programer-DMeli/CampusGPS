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

enum class TipoError {
    SIN_PERMISO,
    GPS_APAGADO,
    SIN_PLAY_SERVICES,
    OTRO
}

//En models clasifica los errores en cuatro categorias. Para decidir que boton de accion mostrar.
