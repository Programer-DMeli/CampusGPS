package com.carbajal.campusgps.data

import android.content.Context
import com.carbajal.campusgps.model.Posicion

interface UbicacionRepository {
    fun playServicesDisponible(context: Context): Boolean //nos permite vereficar
    //si el dispositivo cuenta con google Play service antes de hacer solicitudes de localizacion.
    suspend fun obtenerUbicacionActual(context: Context): Posicion?
}
