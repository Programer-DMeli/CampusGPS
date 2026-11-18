package com.carbajal.campusgps.data

import com.carbajal.campusgps.model.Lugar
import com.carbajal.campusgps.model.Posicion

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares(): List<Lugar> {
        return listOf(   //provehemos lugares
            Lugar(1, "Laboratorio de Cómputo", "Estudio", "Lab de informática", Posicion(-12.0460, -77.0420)),
            Lugar(2, "Biblioteca Central", "Estudio", "Biblioteca principal", Posicion(-12.0470, -77.0430)),
            Lugar(3, "Cafetería Principal", "Comida", "Cafetería central", Posicion(-12.0455, -77.0435)),
            Lugar(4, "Auditorio Magno", "Eventos", "Auditorio del campus", Posicion(-12.0475, -77.0415)),
            Lugar(5, "Cancha Deportiva", "Deportes", "Cancha multideportiva", Posicion(-12.0450, -77.0425))
        )
    }
}
