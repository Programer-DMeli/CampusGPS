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

sealed interface EstadoUbicacion {
    object Inicial : EstadoUbicacion
    object Buscando : EstadoUbicacion
    data class Exito(val latitud: Double, val longitud: Double) : EstadoUbicacion
    data class Error(val mensaje: String) : EstadoUbicacion
    object PermisoDenegado : EstadoUbicacion
}

class MapaViewModel(
    private val repository: UbicacionRepository = UbicacionRepositoryGoogle()
) : ViewModel() {
    private val _estado = MutableStateFlow<EstadoUbicacion>(EstadoUbicacion.Inicial)
    val estado: StateFlow<EstadoUbicacion> = _estado.asStateFlow()

    fun buscar(context: Context? = null) {
        _estado.value = EstadoUbicacion.Buscando
        viewModelScope.launch {
            try {
                val posicion = if (context != null) repository.obtenerUbicacionActual(context) else null
                if (posicion != null) {
                    _estado.value = EstadoUbicacion.Exito(posicion.latitud, posicion.longitud)
                } else {
                    // Posición por defecto en caso de no obtener ubicación en emulador
                    _estado.value = EstadoUbicacion.Exito(-12.046374, -77.042793)
                }
            } catch (e: Exception) {
                _estado.value = EstadoUbicacion.Error(e.message ?: "Error al obtener la ubicación")
            }
        }
    }

    fun permisoDenegado() {
        _estado.value = EstadoUbicacion.PermisoDenegado
    }
}
