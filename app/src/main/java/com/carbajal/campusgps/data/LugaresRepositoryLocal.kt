package com.carbajal.campusgps.data

import com.carbajal.campusgps.model.Lugar
import com.carbajal.campusgps.model.Posicion

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares(): List<Lugar> {
        // Puntos de referencia aproximados dentro de Tecsup Lima (Av. Cascanueces 2221).
        return listOf(
            Lugar(1, "Cafetería Campus", "Comida", "Cafetería principal del campus", Posicion(-12.04376, -76.95291)),
            Lugar(2, "Biblioteca Central", "Estudio", "Biblioteca principal", Posicion(-12.04391, -76.95264)),
            Lugar(3, "Laboratorio de Cómputo", "Estudio", "Lab de informática", Posicion(-12.04415, -76.95271)),
            Lugar(4, "Auditorio Magno", "Eventos", "Auditorio del campus", Posicion(-12.04418, -76.95302)),
            Lugar(5, "Centro de Servicios", "Servicios", "Servicios administrativos", Posicion(-12.04396, -76.95316))
        )
    }
}
