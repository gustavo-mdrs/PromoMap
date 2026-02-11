package com.example.promomap.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigPage(
    // Callbacks de navegação e ação
    onBackClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    // Estados locais de UI (apenas para visualização do Switch mudando)
    // Numa app real, esses valores viriam do ViewModel (ex: viewModel.isNotificationEnabled)
    var notificacoesEnabled by remember { mutableStateOf(true) }
    var modoSateliteEnabled by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val themeColor = Color(0xFF1B5E20) // Verde PromoMap

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Configurações", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Fundo cinza bem claro para destacar os blocos
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            // --- SEÇÃO GERAL ---
            ConfigSectionTitle("Geral")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ConfigSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Receber Notificações",
                        subtitle = "Alertas de promoções próximas",
                        isChecked = notificacoesEnabled,
                        onCheckedChange = { notificacoesEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEÇÃO MAPA ---
            ConfigSectionTitle("Mapa")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ConfigSwitchItem(
                        icon = Icons.Default.Place,
                        title = "Modo Satélite",
                        subtitle = "Exibir mapa com imagens reais",
                        isChecked = modoSateliteEnabled,
                        onCheckedChange = { modoSateliteEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEÇÃO CONTA ---
            ConfigSectionTitle("Conta e Privacidade")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ConfigActionItem(
                        icon = Icons.Default.Info,
                        title = "Sobre o App",
                        onClick = { /* Abrir diálogo Sobre */ }
                    )
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    ConfigActionItem(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, // Ícone genérico
                        title = "Excluir minha conta",
                        textColor = Color(0xFFD32F2F), // Vermelho alerta
                        onClick = onDeleteAccountClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rodapé
            Text(
                text = "PromoMap v1.0.0",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// --- Componentes Auxiliares (Para evitar repetição de código) ---

@Composable
fun ConfigSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1B5E20),
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun ConfigSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1B5E20)
            )
        )
    }
}

@Composable
fun ConfigActionItem(
    icon: ImageVector,
    title: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onClick() }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Preview
@Composable
fun ConfigPagePreview() {
    ConfigPage(onBackClick = {}, onDeleteAccountClick = {})
}
