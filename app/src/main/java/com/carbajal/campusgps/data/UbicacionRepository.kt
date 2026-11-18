package com.carbajal.campusgps.data

import android.content.Context
import com.carbajal.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerUbicacionActual(context: Context): Posicion?
}
