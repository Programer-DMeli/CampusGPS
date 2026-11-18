package com.carbajal.campusgps.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.campusgps.data.LugaresRepository
import com.carbajal.campusgps.data.LugaresRepositoryLocal
import com.carbajal.campusgps.data.UbicacionRepository
import com.carbajal.campusgps.data.UbicacionRepositoryGoogle
import com.carbajal.campusgps.model.LugarConDistancia
import com.carbajal.campusgps.model.Posicion
import com.carbajal.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UbicacionUi {
    object Inicial : UbicacionUi
    object Buscando : UbicacionUi
    data class Lista(val latitud: Double, val longitud: Double) : UbicacionUi
    data class Exito(val latitud: Double, val longitud: Double) : UbicacionUi
    data class Error(val mensaje: String) : UbicacionUi
    object PermisoDenegado : UbicacionUi
}

typealias EstadoUbicacion = UbicacionUi

class MapaViewModel(
    private val repository: UbicacionRepository = UbicacionRepositoryGoogle(),
    private val lugaresRepository: LugaresRepository = LugaresRepositoryLocal()
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow<List<LugarConDistancia>>(
        lugaresRepository.obtenerLugares().map { LugarConDistancia(it) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    fun buscar(context: Context? = null) {
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            try {
                val posicion = if (context != null) repository.obtenerUbicacionActual(context) else null
                val posFinal = posicion ?: Posicion(-12.0464, -77.0428)

                actualizarDistancias(posFinal)

                _estado.value = UbicacionUi.Lista(posFinal.latitud, posFinal.longitud)
            } catch (e: Exception) {
                _estado.value = UbicacionUi.Error(e.message ?: "Error al obtener la ubicación")
            }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        val lugaresBase = lugaresRepository.obtenerLugares()
        val listaConDistancia = lugaresBase.map { lugar ->
            val dist = distanciaMetros(pos, lugar)
            LugarConDistancia(lugar = lugar, distanciaMetros = dist.toInt())
        }.sortedBy { it.distanciaMetros }

        _lugares.value = listaConDistancia
        Log.d("MapaViewModel", "Lugares ordenados por distancia: $listaConDistancia")
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.PermisoDenegado
    }
}
