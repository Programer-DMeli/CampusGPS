package com.carbajal.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.carbajal.campusgps.model.LugarConDistancia
import com.carbajal.campusgps.viewmodel.UbicacionUi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia> = emptyList(),
    seleccionado: Int? = null,
    modifier: Modifier = Modifier
) {
    // Cámara inicial: Lima (-12.0464, -77.0428) con zoom 12
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // TODO 1: LaunchedEffect(estado): si es Lista, animar la cámara (zoom 16f)
    LaunchedEffect(estado) {
        val latLng = when (estado) {
            is UbicacionUi.Lista -> LatLng(estado.latitud, estado.longitud)
            is UbicacionUi.Exito -> LatLng(estado.latitud, estado.longitud)
            else -> null
        }
        if (latLng != null) {
            camara.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara
    ) {
        // TODO 2: si el estado es Lista, un Marker "Estás aquí"
        val miPosicion = when (estado) {
            is UbicacionUi.Lista -> LatLng(estado.latitud, estado.longitud)
            is UbicacionUi.Exito -> LatLng(estado.latitud, estado.longitud)
            else -> null
        }

        if (miPosicion != null) {
            Marker(
                state = rememberMarkerState(position = miPosicion),
                title = "Estás aquí"
            )
        }

        // TODO 3 (Paso 4): un Marker por cada lugar
        for (item in lugares) {
            val posLugar = LatLng(item.lugar.posicion.latitud, item.lugar.posicion.longitud)
            Marker(
                state = rememberMarkerState(position = posLugar),
                title = item.lugar.nombre,
                snippet = "${item.distanciaMetros} m"
            )
        }
    }
}
