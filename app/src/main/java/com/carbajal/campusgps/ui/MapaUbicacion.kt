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

    // LaunchedEffect(seleccionado): anima la cámara hasta ese lugar con zoom 17
    LaunchedEffect(seleccionado) {
        if (seleccionado != null) {
            val lugarSel = lugares.find { it.lugar.id == seleccionado }
            if (lugarSel != null) {
                camara.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lugarSel.lugar.posicion.latitud, lugarSel.lugar.posicion.longitud),
                        17f
                    )
                )
            }
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
            val esElSeleccionado = item.lugar.id == seleccionado
            val esElMasCercano = item.lugar.id == masCercanoId
            val posLugar = LatLng(item.lugar.posicion.latitud, item.lugar.posicion.longitud)
            val markerState = rememberUpdatedMarkerState(position = posLugar)

            LaunchedEffect(esElSeleccionado) {
                if (esElSeleccionado) {
                    markerState.showInfoWindow()
                }
            }

            val colorIcono = if (esElMasCercano) {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            } else {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            }

            key(item.lugar.id) {
                Marker(
                    state = markerState,
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = colorIcono
                )
            }
        }
    }
}
