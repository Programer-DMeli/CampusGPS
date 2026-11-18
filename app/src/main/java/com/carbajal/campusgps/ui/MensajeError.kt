package com.carbajal.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carbajal.campusgps.model.TipoError
import com.carbajal.campusgps.viewmodel.UbicacionUi

@Composable
fun MensajeError(
    //composable que renderiza la tarjeta roja de mensaje en la pantalla
    error: UbicacionUi.Error,
    onAbrirAjustes: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bluePrimary = Color(0xFF00639B)
    val redContainer = Color(0xFFFCE8E6)
    val redText = Color(0xFFD93025)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = redContainer
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = error.mensaje,
                color = redText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            )
        }

        when (error.tipo) {
            TipoError.SIN_PERMISO -> {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onAbrirAjustes,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, bluePrimary)
                ) {
                    Text(
                        text = "Abrir ajustes",
                        color = bluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            TipoError.GPS_APAGADO, TipoError.OTRO -> {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onReintentar,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, bluePrimary)
                ) {
                    Text(
                        text = "Reintentar",
                        color = bluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            TipoError.SIN_PLAY_SERVICES -> {
                // Sin botón de reintentar según especificación
            }
        }
    }
}
