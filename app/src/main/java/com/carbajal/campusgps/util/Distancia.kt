package com.carbajal.campusgps.util

import android.location.Location
import com.carbajal.campusgps.model.Lugar
import com.carbajal.campusgps.model.Posicion


//Se toma el valores de medicion para calcular la distancia entre la ubicacion actual, hasta la ubicacion del mas cercano de la lista para ordenarlos
fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {  //esta funcion devuelve el resultado en metros
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud,
        desde.longitud,
        hasta.posicion.latitud,
        hasta.posicion.longitud,
        resultado
    )
    return resultado[0]
}

//desde tu posicion a cada lugar