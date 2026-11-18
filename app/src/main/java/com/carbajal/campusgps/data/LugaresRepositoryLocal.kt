package com.carbajal.campusgps.data

import com.carbajal.campusgps.model.Lugar
import com.carbajal.campusgps.model.Posicion

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares(): List<Lugar> {
        return listOf(
            Lugar(1, "Cafetería Campus", "Comida", "Cafetería principal del campus", Posicion(-12.0463, -77.0427)),
            Lugar(2, "Biblioteca Central", "Estudio", "Biblioteca principal", Posicion(-12.0466, -77.0430)),
            Lugar(3, "Laboratorio de Cómputo", "Estudio", "Lab de informática", Posicion(-12.0450, -77.0410)),
            Lugar(4, "Auditorio Magno", "Eventos", "Auditorio del campus", Posicion(-12.0485, -77.0445)),
            Lugar(5, "Centro de Servicios", "Servicios", "Servicios administrativos", Posicion(-12.0500, -77.0460))
        )
    }
}
