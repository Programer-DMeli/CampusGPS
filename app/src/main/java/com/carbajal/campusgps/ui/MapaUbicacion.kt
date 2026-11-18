package com.carbajal.campusgps.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.carbajal.campusgps.model.LugarConDistancia
import com.carbajal.campusgps.viewmodel.UbicacionUi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

private val CENTRO_CAMPUS_TECSUP = LatLng(-12.04398, -76.95291)

private const val DARK_MAP_STYLE_JSON = """
[
  { "elementType": "geometry", "stylers": [{ "color": "#242f3e" }] },
  { "elementType": "labels.text.fill", "stylers": [{ "color": "#746855" }] },
  { "elementType": "labels.text.stroke", "stylers": [{ "color": "#242f3e" }] },
  { "featureType": "administrative.locality", "elementType": "labels.text.fill", "stylers": [{ "color": "#d59563" }] },
  { "featureType": "poi", "elementType": "labels.text.fill", "stylers": [{ "color": "#d59563" }] },
  { "featureType": "poi.park", "elementType": "geometry", "stylers": [{ "color": "#263c3f" }] },
  { "featureType": "poi.park", "elementType": "labels.text.fill", "stylers": [{ "color": "#6b9a76" }] },
  { "featureType": "road", "elementType": "geometry", "stylers": [{ "color": "#38414e" }] },
  { "featureType": "road", "elementType": "geometry.stroke", "stylers": [{ "color": "#212a37" }] },
  { "featureType": "road", "elementType": "labels.text.fill", "stylers": [{ "color": "#9ca5b3" }] },
  { "featureType": "road.highway", "elementType": "geometry", "stylers": [{ "color": "#746855" }] },
  { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [{ "color": "#1f2835" }] },
  { "featureType": "road.highway", "elementType": "labels.text.fill", "stylers": [{ "color": "#f3d19c" }] },
  { "featureType": "transit", "elementType": "geometry", "stylers": [{ "color": "#2f3948" }] },
  { "featureType": "transit.station", "elementType": "labels.text.fill", "stylers": [{ "color": "#d59563" }] },
  { "featureType": "water", "elementType": "geometry", "stylers": [{ "color": "#17263c" }] },
  { "featureType": "water", "elementType": "labels.text.fill", "stylers": [{ "color": "#515c6d" }] },
  { "featureType": "water", "elementType": "labels.text.stroke", "stylers": [{ "color": "#17263c" }] }
]
"""

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia> = emptyList(),
    seleccionado: Int? = null,
    masCercanoId: Int? = null,
    modifier: Modifier = Modifier
) {
    val esTemaOscuro = isSystemInDarkTheme()

    val mapProperties = remember(esTemaOscuro) {
        MapProperties(
            mapStyleOptions = if (esTemaOscuro) {
                MapStyleOptions(DARK_MAP_STYLE_JSON)
            } else {
                null
            }
        )
    }

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CENTRO_CAMPUS_TECSUP, 18f)
    }

    // Centra la cámara al obtener la ubicación del usuario.
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

    val lugarSeleccionado = lugares.find { it.lugar.id == seleccionado }?.lugar
    LaunchedEffect(lugarSeleccionado) {
        if (lugarSeleccionado != null) {
            val posicion = lugarSeleccionado.posicion
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(posicion.latitud, posicion.longitud), 18f)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        properties = mapProperties
    ) {
        // Marcador de tu posición en rojo actual
        val miPosicion = when (estado) {
            is UbicacionUi.Lista -> LatLng(estado.latitud, estado.longitud)
            is UbicacionUi.Exito -> LatLng(estado.latitud, estado.longitud)
            else -> null
        }

        if (miPosicion != null) {
            Marker(
                state = rememberUpdatedMarkerState(position = miPosicion),
                title = "Estás aquí"
            )
        }

        // Recorre lugares y dibuja un Marker verde para el más cercano y azul para los demás
        for (item in lugares) {
            val lugar = item.lugar
            key(lugar.id) {
                val posLugar = LatLng(lugar.posicion.latitud, lugar.posicion.longitud)
                val markerState = rememberUpdatedMarkerState(position = posLugar)
                val esElSeleccionado = lugar.id == seleccionado

                LaunchedEffect(esElSeleccionado) {
                    if (esElSeleccionado) {
                        markerState.showInfoWindow()
                    }
                }

                val tono = if (lugar.id == masCercanoId) {
                    BitmapDescriptorFactory.HUE_GREEN
                } else {
                    BitmapDescriptorFactory.HUE_AZURE
                }

                Marker(
                    state = markerState,
                    title = lugar.nombre,
                    snippet = lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(tono)
                )
            }
        }
    }
}
