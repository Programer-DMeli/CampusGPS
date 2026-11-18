package com.carbajal.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carbajal.campusgps.model.LugarConDistancia
import com.carbajal.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercanoId: Int? = null,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bluePrimary = Color(0xFF00639B)
    val greenColor = Color(0xFF34A853)
    val selectedBg = Color(0xFFD7E3FF)

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        items(lugares, key = { it.lugar.id }) { item ->
            val esSeleccionado = item.lugar.id == seleccionado
            val esMasCercano = item.lugar.id == masCercanoId
            val distTexto = if (item.distanciaMetros > 0) {
                formatearDistancia(item.distanciaMetros.toFloat())
            } else {
                "Sin calcular"
            }

            val dotColor = if (esMasCercano) greenColor else bluePrimary

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (esSeleccionado) selectedBg else Color(0xFFF2F4F8)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onClick(item.lugar.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.lugar.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                        Text(
                            text = item.lugar.categoria,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = distTexto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = bluePrimary
                    )
                }
            }
        }
    }
}
