package com.carbajal.campusgps.data

import android.content.Context
import com.carbajal.campusgps.model.Posicion

interface UbicacionRepository {
    fun playServicesDisponible(context: Context): Boolean
    suspend fun obtenerUbicacionActual(context: Context): Posicion?
}
