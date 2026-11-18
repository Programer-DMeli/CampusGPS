package com.carbajal.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.campusgps.data.UbicacionRepository
import com.carbajal.campusgps.data.UbicacionRepositoryGoogle
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
    private val repository: UbicacionRepository = UbicacionRepositoryGoogle()
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    fun buscar(context: Context? = null) {
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            try {
                val posicion = if (context != null) repository.obtenerUbicacionActual(context) else null
                if (posicion != null) {
                    _estado.value = UbicacionUi.Lista(posicion.latitud, posicion.longitud)
                } else {
                    // Posición base por defecto en Lima si no responde ubicación en emulador
                    _estado.value = UbicacionUi.Lista(-12.0464, -77.0428)
                }
            } catch (e: Exception) {
                _estado.value = UbicacionUi.Error(e.message ?: "Error al obtener la ubicación")
            }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.PermisoDenegado
    }
}
