package com.carbajal.campusgps.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.campusgps.data.LugaresRepository
import com.carbajal.campusgps.data.LugaresRepositoryLocal
import com.carbajal.campusgps.data.UbicacionRepository
import com.carbajal.campusgps.data.UbicacionRepositoryGoogle
import com.carbajal.campusgps.model.Lugar
import com.carbajal.campusgps.model.LugarConDistancia
import com.carbajal.campusgps.model.Posicion
import com.carbajal.campusgps.model.TipoError
import com.carbajal.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UbicacionUi {
    object Inicial : UbicacionUi
    object Buscando : UbicacionUi
    data class Lista(val latitud: Double, val longitud: Double) : UbicacionUi
    data class Exito(val latitud: Double, val longitud: Double) : UbicacionUi
    data class Error(val mensaje: String, val tipo: TipoError = TipoError.OTRO) : UbicacionUi
    object PermisoDenegado : UbicacionUi
}

typealias EstadoUbicacion = UbicacionUi  //-->

class MapaViewModel(
    private val repository: UbicacionRepository = UbicacionRepositoryGoogle(),
    private val lugaresRepository: LugaresRepository = LugaresRepositoryLocal()
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _todosLosLugares = MutableStateFlow<List<LugarConDistancia>>(
        lugaresRepository.obtenerLugares().map { LugarConDistancia(it) }
    )

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    val lugares: StateFlow<List<LugarConDistancia>> = combine(
        _todosLosLugares,
        _categoriaSeleccionada
    ) { lista, categoria ->
        if (categoria == null) {
            lista
        } else {
            lista.filter { it.lugar.categoria.equals(categoria, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _todosLosLugares.value
    )

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    val masCercano: StateFlow<Lugar?> = lugares.map { lista ->
        lista.firstOrNull { it.distanciaMetros > 0 }?.lugar
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun buscar(context: Context? = null) {
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            if (context != null && !repository.playServicesDisponible(context)) {
                _estado.value = UbicacionUi.Error(
                    "Este dispositivo no tiene Google Play Services",
                    TipoError.SIN_PLAY_SERVICES
                )
                return@launch
            }

            try {
                val posicion = if (context != null) repository.obtenerUbicacionActual(context) else null
                if (posicion != null) {
                    actualizarDistancias(posicion)
                    _estado.value = UbicacionUi.Lista(posicion.latitud, posicion.longitud)
                } else if (context == null) {
                    val posDefault = Posicion(-12.0464, -77.0428)
                    actualizarDistancias(posDefault)
                    _estado.value = UbicacionUi.Lista(posDefault.latitud, posDefault.longitud)
                } else {
                    _estado.value = UbicacionUi.Error(
                        "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
                        TipoError.GPS_APAGADO
                    )
                }
            } catch (e: Exception) {
                _estado.value = UbicacionUi.Error(
                    "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
                    TipoError.GPS_APAGADO
                )
            }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        val lugaresBase = lugaresRepository.obtenerLugares()
        val listaConDistancia = lugaresBase.map { lugar ->
            val dist = distanciaMetros(pos, lugar)
            LugarConDistancia(lugar = lugar, distanciaMetros = dist.toInt())
        }.sortedBy { it.distanciaMetros }

        _todosLosLugares.value = listaConDistancia
        Log.d("MapaViewModel", "Lugares ordenados por distancia: $listaConDistancia")
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoriaSeleccionada.value = if (categoria.isNullOrBlank() || categoria == "Todas") null else categoria
    }

    fun seleccionar(id: Int?) {  //La funcion seleccionar id para actualizar el ID del lugar seleccionado
        _seleccionado.value = id
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }
}
