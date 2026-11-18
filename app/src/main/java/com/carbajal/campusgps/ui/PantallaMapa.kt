package com.carbajal.campusgps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carbajal.campusgps.model.TipoError
import com.carbajal.campusgps.util.formatearDistancia
import com.carbajal.campusgps.viewmodel.MapaViewModel
import com.carbajal.campusgps.viewmodel.UbicacionUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val categoriaActual by vm.categoriaSeleccionada.collectAsState()
    val contexto = LocalContext.current

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val concedido = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                resultado[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            vm.buscar(contexto)
        } else {
            vm.permisoDenegado()
        }
    }

    fun alPulsar() {
        val tieneFine = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val tieneCoarse = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tieneFine || tieneCoarse) {
            vm.buscar(contexto)
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val bluePrimary = Color(0xFF00639B)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CampusGPS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bluePrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Mapa arriba ocupando el espacio disponible con weight(1f)
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Sección de controles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 6.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chips de filtrado por categoría (Estudio, Comida, Eventos, Servicios)
                val categorias = listOf("Todas", "Estudio", "Comida", "Eventos", "Servicios")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { cat ->
                        val esSeleccionada = (cat == "Todas" && categoriaActual == null) || (cat == categoriaActual)
                        FilterChip(
                            selected = esSeleccionada,
                            onClick = {
                                vm.seleccionarCategoria(if (cat == "Todas") null else cat)
                            },
                            label = {
                                Text(
                                    text = cat,
                                    fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = bluePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF2F4F8),
                                labelColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { alPulsar() },
                    enabled = estado != UbicacionUi.Buscando,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = bluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (estado == UbicacionUi.Buscando) "Buscando..." else "Obtener mi ubicación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Botón Ir al más cercano
                OutlinedButton(
                    onClick = {
                        masCercano?.let { vm.seleccionar(it.id) }
                    },
                    enabled = masCercano != null,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, if (masCercano != null) bluePrimary else Color.LightGray)
                ) {
                    Text(
                        text = "Ir al más cercano",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (masCercano != null) bluePrimary else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Texto del más cercano
                if (masCercano != null) {
                    val lugarConDist = lugares.find { it.lugar.id == masCercano?.id }
                    val textoDist = if (lugarConDist != null && lugarConDist.distanciaMetros > 0) {
                        formatearDistancia(lugarConDist.distanciaMetros.toFloat())
                    } else {
                        ""
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Más cercano: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${masCercano?.nombre} ($textoDist)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF333333)
                        )
                    }
                }

                when (val actual = estado) {
                    is UbicacionUi.Error -> {
                        MensajeError(
                            error = actual,
                            onAbrirAjustes = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", contexto.packageName, null)
                                )
                                contexto.startActivity(intent)
                            },
                            onReintentar = { alPulsar() }
                        )
                    }
                    is UbicacionUi.PermisoDenegado -> {
                        MensajeError(
                            error = UbicacionUi.Error(
                                "Sin permiso de ubicación no puedo mostrar dónde estás",
                                TipoError.SIN_PERMISO
                            ),
                            onAbrirAjustes = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", contexto.packageName, null)
                                )
                                contexto.startActivity(intent)
                            },
                            onReintentar = { alPulsar() }
                        )
                    }
                    else -> {}
                }
            }

            // Lista abajo con altura fija de 220.dp
            ListaLugares(
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                onClick = { id -> vm.seleccionar(id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(220.dp)
            )
        }
    }
}
