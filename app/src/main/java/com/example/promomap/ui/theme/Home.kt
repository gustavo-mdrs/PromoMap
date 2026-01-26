package com.example.promomap.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promomap.R

@Composable
fun HomePage(
    userName: String,
    onNavigateToMap: () -> Unit,
    onNavigateToCadPromo: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- CABEÇALHO (Slide 4 e 7) ---
        // Barra verde com ícones de Configuração, Perfil e Sair
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B5E20)) // Verde escuro do seu tema
                .statusBarsPadding() // Evita ficar embaixo do relógio/bateria
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone Configuração
            IconButton(onClick = onNavigateToConfig) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Configurações",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Ícone Perfil
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Botão Sair
            TextButton(onClick = onLogout) {
                Text(
                    text = "Sair",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- CONTEÚDO PRINCIPAL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Saudação
            Text(
                text = "Bem-vindo/a, $userName!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // CÍRCULO DO MAPA (Botão Principal - Slide 7)
            // Usamos um Box para criar a borda e o formato circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp) // Tamanho grande conforme o PDF
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)) // Fundo verde bem claro
                    .border(8.dp, Color(0xFFA5D6A7), CircleShape) // Borda verde clara
                    .clickable { onNavigateToMap() } // Ação de navegar para o mapa
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Tente usar a imagem do mapa que você tem (R.drawable.imgmapa)
                    // Se não tiver, use um ícone como placeholder
                    Image(
                        painter = painterResource(id = R.drawable.imgmapa), // Certifique-se de ter essa imagem
                        contentDescription = "Ir para o Mapa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // BOTÃO CADASTRAR PROMOÇÃO (Slide 8)
            Button(
                onClick = onNavigateToCadPromo,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF80CBC4) // Verde água do seu design
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Cadastrar Promoção",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}