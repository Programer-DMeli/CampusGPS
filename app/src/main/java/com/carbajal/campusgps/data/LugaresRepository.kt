package com.carbajal.campusgps.data

import com.carbajal.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
