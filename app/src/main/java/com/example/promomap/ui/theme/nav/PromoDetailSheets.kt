package com.example.promomap.ui.theme.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PromoDetailsSheet(
    itemTitle: String,
    price: Double,
    marca: String,
    local: String,
    imageUrl: String?,
    onReportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Cabeçalho (Nome e Preço)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "$marca • $local",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Card de Preço
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "R$ ${String.format("%.2f", price)}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagem (com tratamento se for nula)
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto do produto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem imagem disponível", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de Reportar
        Button(
            onClick = onReportClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Informar que acabou", color = Color.White)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}